package UDD.AleksaColovic.SearchEngine.model;

import UDD.AleksaColovic.SearchEngine.model.common.AbstractDocument;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.UUID;

@Document(indexName = "contract")
@Mapping(mappingPath = "static/contract.json")
@Setting(settingPath = "static/serbian-analyzer-config.json")
@Getter
@Setter
@NoArgsConstructor
public class ContractDocument extends AbstractDocument {
    //region: Fields
    private String signerName;
    private String signerSurname;
    private String governmentName;
    private String administrationLevel;
    private String address;
    private String content;
    private String fileName;
    private GeoLocation location;
    //endregion

    //region: Constructor
    public ContractDocument(UUID id, String signerName, String signerSurname, String governmentName, String administrationLevel, String address, String content, String fileName) {
        this.setId(id);
        this.signerName = signerName;
        this.signerSurname = signerSurname;
        this.governmentName = governmentName;
        this.administrationLevel = administrationLevel;
        this.address = address;
        this.content = content;
        this.fileName = fileName;
    }
    //endregion

}
