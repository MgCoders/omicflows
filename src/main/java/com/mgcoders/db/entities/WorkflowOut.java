package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Embedded;

import java.util.Objects;

/**
 * Created by rsperoni on 13/05/17.
 */
@Embedded
public class WorkflowOut {

    private String name;
    private String schema;

    public WorkflowOut() {
    }

    public WorkflowOut(String name, String schema) {
        this.name = name;
        this.schema = schema;
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
        WorkflowOut that = (WorkflowOut) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
