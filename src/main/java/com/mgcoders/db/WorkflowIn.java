package com.mgcoders.db;

/**
 * Created by rsperoni on 13/05/17.
 */
public class WorkflowIn {

    private String id;
    private String source;
    private String schema;

    public WorkflowIn(String id, String source, String schema) {
        this.id = id;
        this.source = source;
        this.schema = schema;
    }

    public boolean isMapped() {
        return this.source != null && this.source.split("/").length > 1;
    }

    public String getSourceMappedToolName() {
        return this.source.split("/")[0];
    }

    public String getSourceMappedPortName() {
        return this.source.split("/")[1];
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
