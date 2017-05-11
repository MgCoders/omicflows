package com.mgcoders.db;


/**
 * Created by rsperoni on 02/05/17.
 */
public class Tool extends AbstractMongoEntity<Tool> {


    private String name;
    private String cwl;
    private String json;

    public Tool(String name, String cwl) {
        this.name = name;
        this.cwl = cwl;
    }

    public String getName() {
        return name;
    }

    public String getCwl() {
        return cwl;
    }

    public String getJson() {
        return json;
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
