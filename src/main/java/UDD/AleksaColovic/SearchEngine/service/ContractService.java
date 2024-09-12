package UDD.AleksaColovic.SearchEngine.service;

import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import UDD.AleksaColovic.SearchEngine.repository.ContractRepository;
import UDD.AleksaColovic.SearchEngine.service.common.MinioService;
import UDD.AleksaColovic.SearchEngine.service.helpers.SearchHelper;
import UDD.AleksaColovic.SearchEngine.service.interfaces.ISearchService;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService implements ISearchService<ContractDocument> {
    //region: Fields
    private final ContractRepository contractRepository;
    private final MinioService minioService;

    private final SearchHelper<ContractDocument> searchHelper;
    //endregion

    @Override
    public void create(final ContractDocument document) {
        contractRepository.save(document);
    }

    @Override
    public void upload(MultipartFile file) throws Exception {
        if (minioService.loadFile(file.getOriginalFilename()) != null) {
            throw new Exception(String.format("File with the given name already exists %s.", file.getOriginalFilename()));
        }

        minioService.uploadFile(file.getOriginalFilename(), file);

        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();

        create(parseContract(text, file.getOriginalFilename()));
    }

    @Override
    public ContractDocument findById(final UUID id) {
        return contractRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<ContractDocument> findAll() {
        return contractRepository.findAll();
    }

    @Override
    public List<SearchHit<ContractDocument>> search(List<SearchItem> searchItems) {
        Query searchQuery = searchHelper.buildSearchQuery(searchItems);

        NativeQuery nativeQuery = searchHelper.buildNativeQuery(searchQuery);

        SearchHits<ContractDocument> searchHits = searchHelper.runNativeQuery(nativeQuery, ContractDocument.class, "contract");

        return searchHits.getSearchHits();
    }

    @Override
    public void delete(final UUID id) {
        contractRepository.deleteById(id);
    }

    private ContractDocument parseContract(String text, String fileName) {
        List<String> lines = new ArrayList<>(Arrays.stream(text.split("\\r?\\n")).toList());
        lines.removeIf(line -> line.equals(" "));

        String governmentName = lines.get(4).substring(10).stripTrailing();
        String administrationLevel = lines.get(5).substring(13).stripTrailing();
        String address = lines.get(6).stripTrailing();

        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 10; i < (lines.size() - 4); i++) {
            contentBuilder.append(lines.get(i));
        }
        String content = contentBuilder.toString();

        String signerName = lines.get(lines.size() - 4).split(" ")[0].stripTrailing();
        String signerSurname = lines.get(lines.size() - 4).split(" ")[1].stripTrailing();

        return new ContractDocument(
                UUID.randomUUID(),
                signerName,
                signerSurname,
                governmentName,
                administrationLevel,
                address,
                content,
                fileName
        );
    }
}
