package UDD.AleksaColovic.SearchEngine.service.helpers;

import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import co.elastic.clients.elasticsearch._types.GeoDistanceType;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.GeoDistanceQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchHelper<T> {
    private final ElasticsearchOperations elasticsearchOperations;

    public Query buildSearchQuery(List<SearchItem> searchItems) {
        return BoolQuery.of(q -> q.must(builder -> builder.bool(boolBuilder -> {
            searchItems.forEach(searchItem -> {
                switch (searchItem.getOperation()) {
                    case AND -> {
                        boolBuilder.must(mustBuilder -> mustBuilder.match(
                                mustMatchBuilder -> mustMatchBuilder.field(searchItem.getField()).query(searchItem.getValue())));
                        break;
                    }
                    case OR -> {
                        boolBuilder.should(shouldBuilder -> shouldBuilder.match(
                                shouldMatchBuilder -> shouldMatchBuilder.field(searchItem.getField()).query(searchItem.getValue())));
                        break;
                    }
                    case NOT -> {
                        boolBuilder.mustNot(mustNotBuilder -> mustNotBuilder.match(
                                mustNotMatchBuilder -> mustNotMatchBuilder.field(searchItem.getField()).query(searchItem.getValue())));
                        break;
                    }
                }
            });
            return boolBuilder;
        })))._toQuery();
    }

    public Query addLocationFilter(Query query, GeoPoint point, Double radius) {
        GeoDistanceQuery geoDistanceQuery = GeoDistanceQuery.of(builder -> builder.field("location").location(builder1 -> builder1.latlon(builder2 -> builder2.lon(point.getLon()).lat(point.getLat()))).distance(radius.toString() + "km").distanceType(GeoDistanceType.Arc));
        return BoolQuery.of(builder -> builder.must(query).filter(geoDistanceQuery._toQuery()))._toQuery();
    }

    public NativeQuery buildNativeQuery(Query query) {
        return NativeQuery.builder()
                .withQuery(query)
                .build();
    }

    public SearchHits<T> runNativeQuery(NativeQuery nativeQuery, Class<T> clazz, String indexName) {
        return elasticsearchOperations.search(nativeQuery, clazz, IndexCoordinates.of(indexName));
    }
}
