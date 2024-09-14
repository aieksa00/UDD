package UDD.AleksaColovic.SearchEngine.service;

import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import UDD.AleksaColovic.SearchEngine.repository.ContractRepository;
import UDD.AleksaColovic.SearchEngine.service.common.MinioService;
import UDD.AleksaColovic.SearchEngine.service.helpers.LocationHelper;
import UDD.AleksaColovic.SearchEngine.service.helpers.SearchHelper;
import UDD.AleksaColovic.SearchEngine.service.interfaces.ISearchService;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ContractService implements ISearchService<ContractDocument> {
    //region: Fields
    private final ContractRepository contractRepository;
    private final MinioService minioService;
    private final String bucketName = "contracts";

    private final SearchHelper<ContractDocument> searchHelper;
    private final LocationHelper locationHelper;
    //endregion

    @Override
    public void upload(MultipartFile file) throws Exception {
        if (minioService.checkIfExists(file.getOriginalFilename(), bucketName)) {
            throw new Exception(String.format("Contract file with the given file name: [%s] already exists.", file.getOriginalFilename()));
        }
        if (contractRepository.findByFileName(file.getOriginalFilename()) != null){
            throw new Exception(String.format("Contract index with the given file name: [%s] already exists.", file.getOriginalFilename()));
        }

        minioService.uploadFile(file.getOriginalFilename(), file, bucketName);

        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();

        ContractDocument contract = parseContract(text);
        contract.setFileName(file.getOriginalFilename());

        Point point = locationHelper.getLatAndLon(contract.getAddress());
        if(point != null){
            GeoPoint geoPoint = GeoPoint.fromPoint(point);
            contract.setLocation(geoPoint);
        }

        contractRepository.save(contract);
    }

    @Override
    public ContractDocument findById(final UUID id) {
        return contractRepository.findById(id).orElse(null);
    }

    @Override
    public Page<ContractDocument> findAll(Pageable pageable) {
        return contractRepository.findAll(pageable);
    }

    @Override
    public List<SearchHit<ContractDocument>> search(List<SearchItem> searchItems, Double radius, Pageable pageable) throws Exception {
        GeoPoint point = null;

        if(radius != null){
            var address = searchItems.stream().filter(searchItem -> searchItem.getField().equals("address")).findFirst();
            if(address.isPresent()){
                searchItems.remove(address.get());
                try {
                    point = GeoPoint.fromPoint(locationHelper.getLatAndLon(address.get().getValue()));

                } catch (Exception e) {
                    throw new Exception("Error while getting a Location from Address");
                }

            }
        }

        Query searchQuery = searchHelper.buildSearchQuery(searchItems);

        NativeQuery nativeQuery = searchHelper.buildNativeQuery(searchQuery, pageable);

        SearchHits<ContractDocument> searchHits = searchHelper.runNativeQuery(nativeQuery, ContractDocument.class, "contract");

        return searchHits.getSearchHits();
    }

    @Override
    public void delete(final UUID id) throws Exception{
        Optional<ContractDocument> document = contractRepository.findById(id);
        if (document.isEmpty()){
            throw new Exception(String.format("Contract index with the given id: [%s] does not exist.", id));
        }
        if (!minioService.checkIfExists(document.get().getFileName(), bucketName)) {
            throw new Exception(String.format("Contract file with the given id: [%s] does not exist.", id));
        }

        minioService.deleteFile(document.get().getFileName(), bucketName);
        contractRepository.deleteById(id);
    }

    private ContractDocument parseContract(String text) {
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
                content
        );
    }
}
