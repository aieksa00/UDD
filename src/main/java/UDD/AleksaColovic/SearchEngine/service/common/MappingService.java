package UDD.AleksaColovic.SearchEngine.service.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class MappingService {
    private static  final Logger LOG = LoggerFactory.getLogger(MappingService.class);
    private final ResourceLoader resourceLoader;

    public MappingService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public InputStream read(final String filePath) {
        Resource res = resourceLoader.getResource("classpath:" + filePath);

        try {
            return res.getInputStream();
        } catch (final IOException e) {
            LOG.error("{}", e.getMessage(), e);
            return null;
        }
    }
}
