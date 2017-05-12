package com.mgcoders.db;


import java.io.IOException;

import static com.mgcoders.utils.YamlUtils.cwlFileContentToJson;

/**
 * Created by rsperoni on 02/05/17.
 */
public class Tool extends AbstractMongoEntity<Tool> {


    private String name;
    private String cwl;
    private String json;

    public Tool(String name, String cwl, String json) {
        this.name = name;
        this.cwl = cwl;
        this.json = json;
    }

    public Tool() {
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

    public void generateJson() throws IOException {
        this.json = cwlFileContentToJson(cwl);
    }

    @Override
    public String toString() {
        return "Tool{" +
                "name='" + name + '\'' +
                ", cwl='" + cwl + '\'' +
                ", _id=" + _id +
                '}';
    }
}
