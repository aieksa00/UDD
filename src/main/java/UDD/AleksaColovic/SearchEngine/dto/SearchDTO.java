package UDD.AleksaColovic.SearchEngine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.OptionalDouble;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
    private List<String> searchParams;
    private Double radius;
}
