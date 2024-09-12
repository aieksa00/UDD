package UDD.AleksaColovic.SearchEngine.converter;

import UDD.AleksaColovic.SearchEngine.dto.LawDTO;
import UDD.AleksaColovic.SearchEngine.model.LawDocument;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class LawConverter {

    public LawDocument toDocument(final LawDTO dto){
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
        return new LawDocument(id, dto.getContent(), dto.getFileName());
    }

    public LawDTO toDTO(final LawDocument document) {
        if (document == null) {
            return null;
        }

        return new LawDTO(document.getId().toString(), document.getContent(), document.getFileName());
    }
}
