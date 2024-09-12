package UDD.AleksaColovic.SearchEngine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTO {
    private String id;
    private String signerName;
    private String signerSurname;
    private String governmentName;
    private String administrationLevel;
    private String address;
    private String content;
    private String fileName;
    private Point geoPoint;
}
