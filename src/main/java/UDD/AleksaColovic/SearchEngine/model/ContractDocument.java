package UDD.AleksaColovic.SearchEngine.model;

import UDD.AleksaColovic.SearchEngine.model.common.AbstractDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.util.UUID;

@Document(indexName = "contract")
@Mapping(mappingPath = "static/contract.json")
@Getter
@Setter
public class ContractDocument extends AbstractDocument {

    private String signerName;
    private String signerSurname;
    private String governmentName;
    private String administrationLevel;
    private String address;
    private String content;
    private String fileName;

    //region: Constructor
    public ContractDocument(String id, String signerName, String signerSurname, String governmentName, String administrationLevel, String address, String content, String fileName) {
        if (!IsValid(id)) {
            return;
        }

        this.setId(UUID.fromString(id));
        this.signerName = signerName;
        this.signerSurname = signerSurname;
        this.governmentName = governmentName;
        this.administrationLevel = administrationLevel;
        this.address = address;
        this.content = content;
        this.fileName = fileName;
    }

    private boolean IsValid(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //endregion

}
