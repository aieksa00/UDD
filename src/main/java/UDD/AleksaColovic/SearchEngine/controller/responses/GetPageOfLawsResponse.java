package UDD.AleksaColovic.SearchEngine.controller.responses;

import UDD.AleksaColovic.SearchEngine.dto.LawDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPageOfLawsResponse {
    private List<LawDTO> laws;
    private int currentPage;
    private int pageSize;
    private int totalItems;
    private int totalPages;
}
