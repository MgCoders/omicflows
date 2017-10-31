package coop.magnesium.db.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import coop.magnesium.utils.YamlUtils;
import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by rsperoni on 02/05/17.
 */
@Entity
public class Tool implements Serializable {

    @Id
    private String id;
    private String name;
    private String cwl;
    @JsonIgnore
    private String json;
    private String descripcion;

    public Tool(String name, String cwl, String json) {
        this.name = name;
        this.cwl = cwl;
        this.json = json;
    }

    public Tool() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCwl() {
        return cwl;
    }

    public void setCwl(String cwl) {
        this.cwl = cwl;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void generateJson() throws IOException {
        this.json = YamlUtils.cwlFileContentToJson(cwl);
    }

    @Override
    public String toString() {
        return "Tool{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cwl='" + cwl + '\'' +
                ", json='" + json + '\'' +
                '}';
    }
}
