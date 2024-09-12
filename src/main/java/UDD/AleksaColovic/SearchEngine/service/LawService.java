package UDD.AleksaColovic.SearchEngine.service;

import UDD.AleksaColovic.SearchEngine.model.LawDocument;
import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import UDD.AleksaColovic.SearchEngine.repository.LawRepository;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LawService implements ISearchService<LawDocument> {
    private final LawRepository lawRepository;
    private final MinioService minioService;

    private final SearchHelper<LawDocument> searchHelper;

    @Override
    public void create(LawDocument document) {
        lawRepository.save(document);
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

        create(new LawDocument(UUID.randomUUID(), text, file.getOriginalFilename()));
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
    public List<SearchHit<LawDocument>> search(List<SearchItem> searchItems) {
        Query searchQuery = searchHelper.buildSearchQuery(searchItems);

        NativeQuery nativeQuery = searchHelper.buildNativeQuery(searchQuery);

        SearchHits<LawDocument> searchHits = searchHelper.runNativeQuery(nativeQuery, LawDocument.class, "law");

        return searchHits.getSearchHits();
    }

    @Override
    public void delete(UUID id) {
        lawRepository.deleteById(id);
    }
}
