package UDD.AleksaColovic.SearchEngine.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DocumentService {
    private static  final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    public Map<String, String> getDocumentInfo() {
        final var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));

        final Set<BeanDefinition> documentDefinitions = scanner.findCandidateComponents("UDD.AleksaColovic.SearchEngine.model");

        Map<String, String> documentInfo = new HashMap<>(10);
        for (final BeanDefinition definition : documentDefinitions) {
            try {
                final Class<?> documentClass = Class.forName(definition.getBeanClassName());
                String indexName = extractIndexName(documentClass);
                String mappingPath = extractMappingPath(documentClass);

                if(indexName != null && mappingPath != null) {
                    documentInfo.put(indexName, mappingPath);
                }

            } catch (ClassNotFoundException e) {
                LOG.error("{}",e.getMessage(), e);
            }
        }

        return documentInfo;
    }

    private String extractMappingPath(final Class<?> documentClass) {
        final Mapping annotation = documentClass.getAnnotation(Mapping.class);
        if (annotation == null) {
            return null;
        }

        return annotation.mappingPath();
    }

    private String extractIndexName(final Class<?> documentClass) {
        final Document annotation = documentClass.getAnnotation(Document.class);
        if (annotation == null) {
            return null;
        }

        return annotation.indexName();
    }
}
