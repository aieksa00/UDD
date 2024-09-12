package UDD.AleksaColovic.SearchEngine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LawDTO {
    private String id;
    private String content;
    private String fileName;
}
