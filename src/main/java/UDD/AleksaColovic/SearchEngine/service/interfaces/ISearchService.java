package UDD.AleksaColovic.SearchEngine.service.interfaces;

import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;

public interface ISearchService<T> {
    void upload(MultipartFile file) throws Exception;

    T findById(final UUID id);

    Page<T> findAll(Pageable pageable);

    List<SearchHit<T>> search(List<SearchItem> searchItems, Double radius, Pageable pageable) throws Exception;

    void delete(final UUID id) throws Exception;
}
