package com.mgcoders.db;

/**
 * Created by rsperoni on 13/05/17.
 */
public class WorkflowOut {

    private String id;
    private String schema;

    public WorkflowOut(String id, String schema) {
        this.id = id;
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
