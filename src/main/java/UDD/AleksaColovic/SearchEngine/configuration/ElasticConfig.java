package UDD.AleksaColovic.SearchEngine.configuration;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;

@Configuration
@EnableElasticsearchRepositories(basePackages = "UDD.AleksaColovic.SearchEngine.repository")
public class ElasticConfig extends ElasticsearchConfiguration {

    @NotNull
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedToLocalhost()
                .usingSsl(buildSSLContext())
                .withBasicAuth("elastic", "03pDMtwNzq=ity2q7NEI")
                .build();
    }

    private static SSLContext buildSSLContext() {
        try {
            return new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
