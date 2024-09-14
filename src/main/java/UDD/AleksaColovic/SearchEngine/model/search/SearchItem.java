package UDD.AleksaColovic.SearchEngine.model.search;

import UDD.AleksaColovic.SearchEngine.model.enums.DocumentSearchOperation;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchItem {
    private String field;
    private String value;
    private DocumentSearchOperation operation;
    private boolean isPhrase;
}


