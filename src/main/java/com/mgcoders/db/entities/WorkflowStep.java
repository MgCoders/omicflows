package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Embedded;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rsperoni on 13/05/17.
 */
@Embedded
public class WorkflowStep {

    private String id;
    private String name;
    private String json;
    private List<WorkflowIn> neededInputs = new ArrayList<>();
    private List<WorkflowOut> neededOutputs = new ArrayList<>();

    public WorkflowStep() {
        //Este id es para distinguir en el frontend, no para la bd ya que va embedded.
        this.name = UUID.randomUUID().toString().split("-")[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<WorkflowIn> getNeededInputs() {
        return neededInputs;
    }

    public void setNeededInputs(List<WorkflowIn> neededInputs) {
        this.neededInputs = neededInputs;
    }

    public List<WorkflowOut> getNeededOutputs() {
        return neededOutputs;
    }

    public void setNeededOutputs(List<WorkflowOut> neededOutputs) {
        this.neededOutputs = neededOutputs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "WorkflowStep{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", neededInputs=" + neededInputs +
                ", neededOutputs=" + neededOutputs +
                '}';
    }
}
