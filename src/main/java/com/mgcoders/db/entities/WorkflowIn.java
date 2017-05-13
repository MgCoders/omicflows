package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Embedded;

/**
 * Created by rsperoni on 13/05/17.
 */
@Embedded
public class WorkflowIn extends AbstractMongoEntity<WorkflowIn> {

    private String name;
    private String sourceMappedToolName;
    private String sourceMappedPortName;
    private Boolean mapped;
    private String schema;

    public WorkflowIn(String name, String sourceMappedToolName, String sourceMappedPortName, Boolean mapped, String schema) {
        this.name = name;
        this.sourceMappedToolName = sourceMappedToolName;
        this.sourceMappedPortName = sourceMappedPortName;
        this.schema = schema;
        this.mapped = mapped;
    }




    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceMappedToolName() {
        return sourceMappedToolName;
    }

    public void setSourceMappedToolName(String sourceMappedToolName) {
        this.sourceMappedToolName = sourceMappedToolName;
    }

    public String getSourceMappedPortName() {
        return sourceMappedPortName;
    }

    public void setSourceMappedPortName(String sourceMappedPortName) {
        this.sourceMappedPortName = sourceMappedPortName;
    }

    public Boolean getMapped() {
        return mapped;
    }

    public void setMapped(Boolean mapped) {
        this.mapped = mapped;
    }
}
