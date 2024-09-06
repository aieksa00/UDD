package UDD.AleksaColovic.SearchEngine.converter;

import UDD.AleksaColovic.SearchEngine.dto.ContractDTO;
import UDD.AleksaColovic.SearchEngine.model.ContractDocument;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ContractConverter {
    public ContractDocument toDocument(final ContractDTO dto){
        if (dto == null) {
            return null;
        }

        String id = dto.id();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        return new ContractDocument(id, dto.signerName(), dto.signerSurname(), dto.governmentName(), dto.administrationLevel(), dto.address(), dto.content(), dto.fileName());
    }

    public ContractDTO toDTO(final ContractDocument document){
        if (document == null) {
           return null;
        }

        return new ContractDTO(document.getId().toString(), document.getSignerName(), document.getSignerSurname(), document.getGovernmentName(), document.getAdministrationLevel(), document.getAddress(), document.getContent(), document.getFileName());
    }
}
