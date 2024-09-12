package UDD.AleksaColovic.SearchEngine.converter;

import UDD.AleksaColovic.SearchEngine.dto.ContractDTO;
import UDD.AleksaColovic.SearchEngine.dto.LawDTO;
import UDD.AleksaColovic.SearchEngine.dto.SearchDTO;
import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import UDD.AleksaColovic.SearchEngine.model.LawDocument;
import UDD.AleksaColovic.SearchEngine.model.enums.DocumentSearchOperation;
import UDD.AleksaColovic.SearchEngine.model.search.SearchItem;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchConverter {

    public List<SearchItem> createSearchItems(SearchDTO dto) {
        List<SearchItem> searchItems = new ArrayList<>(dto.getSearchParams().size());
        List<String> searchParams = dto.getSearchParams();

        for (int i = 0; i < searchParams.size(); i++) {
            if (i % 2 == 0) continue;
            String searchParam = searchParams.get(i);
            String field = searchParam.split(":")[0].strip();
            String value = searchParam.split(":")[1].strip();
            DocumentSearchOperation operation = DocumentSearchOperation.valueOf(searchParams.get(i - 1));
            searchItems.add(new SearchItem(field, value, operation));
        }

        return searchItems;
    }

    public List<ContractDTO> convertToContractDTOs(List<SearchHit<ContractDocument>> hits) {
        return hits.stream()
                .map(hit -> {
                    ContractDocument document = hit.getContent();
                    return ContractDTO.builder()
                            .id(document.getId().toString())
                            .signerName(document.getSignerName())
                            .signerSurname(document.getSignerSurname())
                            .governmentName(document.getGovernmentName())
                            .administrationLevel(document.getAdministrationLevel())
                            .address(document.getAddress())
                            .content(document.getContent())
                            .fileName(document.getFileName())
                            .geoPoint(GeoPoint.toPoint(document.getLocation()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<LawDTO> convertToLawDTOs(List<SearchHit<LawDocument>> hits) {
        return hits.stream()
                .map(hit -> {
                    LawDocument document = hit.getContent();
                    return LawDTO.builder()
                            .id(document.getId().toString())
                            .content(document.getContent())
                            .fileName(document.getFileName())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
