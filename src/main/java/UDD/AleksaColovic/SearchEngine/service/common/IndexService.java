package UDD.AleksaColovic.SearchEngine.service.common;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.analysis.*;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.IndexSettingsAnalysis;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    private final ElasticsearchClient client;
    private final MappingService mappingService;
    private final DocumentService documentService;

    @PostConstruct
    public void setupIndices() {
        final Map<String, String> documentInfo = documentService.getDocumentInfo();

        for (final Map.Entry<String, String> entry : documentInfo.entrySet()) {
            String indexName = entry.getKey();
            String mappingPath = entry.getValue();

            try {
                final BooleanResponse exists = client.indices().exists(r -> r.index(indexName));
                if (exists.value()) {
                    continue;
                }

                createIndex(indexName, mappingPath);
                LOG.info("Index {} created successfully!", indexName);
            }
            catch (IOException e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
    }

    public Set<String> recreateIndices() {
        final Map<String, String> documentInfo = documentService.getDocumentInfo();

        for (final Map.Entry<String, String> entry : documentInfo.entrySet()) {
            String indexName = entry.getKey();
            String mappingPath = entry.getValue();

            try {
                client.indices().delete(d -> d.index(indexName));
                createIndex(indexName, mappingPath);
                LOG.info("Index {} recreated successfully!", indexName);
            }
            catch (IOException e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }

        return documentInfo.keySet();
    }

    private void createIndex(String indexName, String mappingPath) throws IOException {
        client.indices().create(buildCreateIndexRequest(indexName, mappingPath));
    }

    private CreateIndexRequest buildCreateIndexRequest(String indexName, String mappingPath) {
        return new CreateIndexRequest.Builder()
                .index(indexName)
                .settings(buildIndexSettings())
                .mappings(buildMappings(mappingPath))
                .build();
    }

    private TypeMapping buildMappings(String mappingPath) {
        return new TypeMapping.Builder()
                .withJson(mappingService.read(mappingPath))
                .build();
    }

    private IndexSettings buildIndexSettings() {
        // Define the custom filter
        TokenFilter serbianCyrillicToLatinicFilter = TokenFilter.of(b -> b
                .definition(db -> db
                        .icuTransform(builder -> builder
                                .id("Any-Latin; NFD; [:Nonspacing Mark:] Remove; NFC"))));

        // Define the custom analyzer
        Analyzer serbianSimpleAnalyzer = AnalyzerBuilders.custom()
                .tokenizer("standard")
                .filter("serbian_cyrillic_to_latinic", "icu_folding", "lowercase")
                .build()
                ._toAnalyzer();


        // Build the analysis settings
        IndexSettingsAnalysis analysis = IndexSettingsAnalysis.of(b -> b
                        .filter("serbian_cyrillic_to_latinic", serbianCyrillicToLatinicFilter)
                        .analyzer("serbian_simple", serbianSimpleAnalyzer));

        // Build the index settings
        return IndexSettings.of(is -> is.analysis(analysis));
    }
}
