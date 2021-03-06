package coop.magnesium.db.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.dozd.mongo.annotation.Embedded;

/**
 * Created by rsperoni on 07/11/17.
 */
@Embedded
public class JobResource {
    private String name;
    @JsonProperty("class")
    private String _class;
    private String value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
