package UDD.AleksaColovic.SearchEngine.model;

import UDD.AleksaColovic.SearchEngine.model.common.AbstractDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.UUID;

@Document(indexName = "law")
@Mapping(mappingPath = "static/law.json")
@Setting(settingPath = "static/serbian-analyzer-config.json")
@Getter
@Setter
@NoArgsConstructor
public class LawDocument extends AbstractDocument {
    //region: Fields
    private String content;
    private String fileName;
    //endregion

    //region: Constructor
    public LawDocument(UUID id, String content, String fileName) {
        this.setId(id);
        this.content = content;
        this.fileName = fileName;
    }
    //endregion
}
