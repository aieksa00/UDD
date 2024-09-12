package UDD.AleksaColovic.SearchEngine.service.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpRequestFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LocationHelper {
    private final String url = "https://us1.locationiq.com/v1/search";
    private final String apiKey = "pk.6638f5d97f2e945e19cf4c45737ce134";

    private final RestTemplate restTemplate;

    public Point getLatAndLon(String location) throws Exception {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);

        URI requestUri = new URI(url + "?key=" + apiKey + "&q=" + encodedLocation + "&format=json");

        ResponseEntity<String> response = restTemplate.getForEntity(requestUri, String.class);
        String content = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode contentJson = objectMapper.readTree(content);
        JsonNode locationJson = contentJson.get(0);

        return new Point(locationJson.get("lat").asDouble(), locationJson.get("lon").asDouble());
    }

}
