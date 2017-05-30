package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Embedded;

import java.util.Objects;

/**
 * Created by rsperoni on 13/05/17.
 */
@Embedded
public class WorkflowIn {

    private String name;
    private String sourceMappedToolName;
    private String sourceMappedPortName;
    private Boolean mapped;
    private String schema;

    public WorkflowIn() {
    }

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

    /**
     * Los nombres de los puertos son unicos,
     * es importante para el borrado.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkflowIn that = (WorkflowIn) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "WorkflowIn{" +
                "name='" + name + '\'' +
                ", sourceMappedToolName='" + sourceMappedToolName + '\'' +
                ", sourceMappedPortName='" + sourceMappedPortName + '\'' +
                ", mapped=" + mapped +
                ", schema='" + schema + '\'' +
                '}';
    }
}
