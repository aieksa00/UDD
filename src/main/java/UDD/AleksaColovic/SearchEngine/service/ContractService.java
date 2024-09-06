package UDD.AleksaColovic.SearchEngine.service;

import UDD.AleksaColovic.SearchEngine.converter.ContractConverter;
import UDD.AleksaColovic.SearchEngine.dto.ContractDTO;
import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import UDD.AleksaColovic.SearchEngine.repository.ContractRepository;
import UDD.AleksaColovic.SearchEngine.service.common.MinioService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractConverter converter;
    private final ContractRepository repository;
    private final MinioService minioService;

    public void create(final ContractDTO dto) {
        final ContractDocument document = converter.toDocument(dto);
        repository.save(document);
    }

    public void upload(MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }

        try {

            if (minioService.loadFile(file.getOriginalFilename()) != null) {
                return;
            }

            minioService.uploadFile(file.getOriginalFilename(), file);

            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();

            List<String> lines = new ArrayList<>(Arrays.stream(text.split("\\r?\\n")).toList());
            lines.removeIf(line -> line.equals(" "));

            ContractDTO dto = parseContract(lines, file.getOriginalFilename());

            create(dto);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ContractDTO parseContract(List<String> lines, String fileName) {
        String governmentName = lines.get(4).substring(10).stripTrailing();
        String administrationLevel = lines.get(5).substring(13).stripTrailing();
        String address = lines.get(6).stripTrailing();

        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 10; i < (lines.size() - 4); i++) {
            contentBuilder.append(lines.get(i));
        }
        String content = contentBuilder.toString();

        String signerName = lines.get(lines.size()-4).split(" ")[0].stripTrailing();
        String signerSurname = lines.get(lines.size()-4).split(" ")[1].stripTrailing();

        return new ContractDTO(
                null,
                signerName,
                signerSurname,
                governmentName,
                administrationLevel,
                address,
                content,
                fileName
        );
    }

    public void delete(final UUID id) {
        repository.deleteById(id);
    }

    public ContractDTO findById(final UUID id) {
        final ContractDocument document = repository.findById(id).orElse(null);

        return converter.toDTO(document);
    }

    public List<ContractDTO> findAll() {
        final List<ContractDTO> dtos = new ArrayList<>();

        for (final ContractDocument document : repository.findAll()) {
            dtos.add(converter.toDTO(document));
        }

        return dtos;
    }


}
