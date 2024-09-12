package UDD.AleksaColovic.SearchEngine.service;

import UDD.AleksaColovic.SearchEngine.model.LawDocument;
import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import UDD.AleksaColovic.SearchEngine.repository.LawRepository;
import UDD.AleksaColovic.SearchEngine.service.common.MinioService;
import UDD.AleksaColovic.SearchEngine.service.helpers.SearchHelper;
import UDD.AleksaColovic.SearchEngine.service.interfaces.ISearchService;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LawService implements ISearchService<LawDocument> {
    //region: Fields
    private final LawRepository lawRepository;
    private final MinioService minioService;
    private final String bucketName = "laws";

    private final SearchHelper<LawDocument> searchHelper;
    //endregion

    @Override
    public void upload(MultipartFile file) throws Exception {
        if (minioService.checkIfExists(file.getOriginalFilename(), bucketName)) {
            throw new Exception(String.format("Law file with the given file name: [%s] already exists.", file.getOriginalFilename()));
        }
        if (lawRepository.findByFileName(file.getOriginalFilename()) != null){
            throw new Exception(String.format("Law index with the given file name: [%s] already exists.", file.getOriginalFilename()));
        }

        minioService.uploadFile(file.getOriginalFilename(), file, bucketName);

        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();

        lawRepository.save(new LawDocument(UUID.randomUUID(), text, file.getOriginalFilename()));
    }

    @Override
    public LawDocument findById(UUID id) {
        return lawRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<LawDocument> findAll() {
        return lawRepository.findAll();
    }

    @Override
    public List<SearchHit<LawDocument>> search(List<SearchItem> searchItems, OptionalDouble radius) {
        Query searchQuery = searchHelper.buildSearchQuery(searchItems);

        NativeQuery nativeQuery = searchHelper.buildNativeQuery(searchQuery);

        SearchHits<LawDocument> searchHits = searchHelper.runNativeQuery(nativeQuery, LawDocument.class, "law");

        return searchHits.getSearchHits();
    }

    @Override
    public void delete(UUID id) throws Exception {
        Optional<LawDocument> document = lawRepository.findById(id);
        if (document.isEmpty()){
            throw new Exception(String.format("Law index with the given id: [%s] does not exist.", id));
        }
        if (!minioService.checkIfExists(document.get().getFileName(), bucketName)) {
            throw new Exception(String.format("Law file with the given id: [%s] does not exist.", id));
        }

        minioService.deleteFile(document.get().getFileName(), bucketName);
        lawRepository.deleteById(id);
    }
}
