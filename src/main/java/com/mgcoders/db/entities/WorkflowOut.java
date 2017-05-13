package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Embedded;

/**
 * Created by rsperoni on 13/05/17.
 */
@Embedded
public class WorkflowOut extends AbstractMongoEntity<WorkflowOut> {

    private String name;
    private String schema;

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
}
