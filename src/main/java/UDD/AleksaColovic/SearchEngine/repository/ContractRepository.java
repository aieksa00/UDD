package UDD.AleksaColovic.SearchEngine.repository;

import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface ContractRepository extends ElasticsearchRepository<ContractDocument, UUID> {
}
