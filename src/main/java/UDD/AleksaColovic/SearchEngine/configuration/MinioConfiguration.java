package UDD.AleksaColovic.SearchEngine.configuration;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class MinioConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MinioConfiguration.class);

    @Value("${spring.minio.url}")
    private String minioHost;

    @Value("${spring.minio.bucket}")
    private String bucketName;

    @Value("${spring.minio.access-key}")
    private String minioAccessKey;

    @Value("${spring.minio.secret-key}")
    private String minioSecretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioHost)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }

    @Bean
    public List<String> checkBuckets(MinioClient minioClient) {
        try {
            List<String> bucketNames = minioClient.listBuckets().stream()
                    .map(Bucket::name)
                    .collect(Collectors.toList());
            bucketNames.forEach(bucket -> logger.info("Bucket: " + bucket));
            return bucketNames;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Error listing buckets", e);
            return List.of(); // Return an empty list in case of error
        }
    }
}
