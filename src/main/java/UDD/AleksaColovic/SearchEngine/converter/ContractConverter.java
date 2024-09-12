package UDD.AleksaColovic.SearchEngine.converter;

import UDD.AleksaColovic.SearchEngine.dto.ContractDTO;
import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ContractConverter {
    public ContractDocument toDocument(final ContractDTO dto){
        if (dto == null) {
            return null;
        }

        UUID id;
        if (dto.getId() == null) {
            id = UUID.randomUUID();
        }
        else {
            id = UUID.fromString(dto.getId());
        }
        return new ContractDocument(id, dto.getSignerName(), dto.getSignerSurname(), dto.getGovernmentName(), dto.getAdministrationLevel(), dto.getAddress(), dto.getContent(), dto.getFileName());
    }

    public ContractDTO toDTO(final ContractDocument document) {
        if (document == null) {
           return null;
        }

        return new ContractDTO(document.getId().toString(), document.getSignerName(), document.getSignerSurname(), document.getGovernmentName(), document.getAdministrationLevel(), document.getAddress(), document.getContent(), document.getFileName(), GeoPoint.toPoint(document.getLocation()));
    }
}
