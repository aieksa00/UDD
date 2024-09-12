package UDD.AleksaColovic.SearchEngine.repository;

import UDD.AleksaColovic.SearchEngine.model.LawDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface LawRepository extends ElasticsearchRepository<LawDocument, UUID> {
}
