package UDD.AleksaColovic.SearchEngine.model.common;

import java.util.UUID;

public abstract class AbstractDocument {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
