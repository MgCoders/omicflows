package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Embedded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 13/05/17.
 */
@Embedded
public class WorkflowStep {

    private String name;
    private String cwl;
    private String json;
    private List<WorkflowIn> innerUnmatchedInputs = new ArrayList<>();
    private List<WorkflowIn> neededInputs = new ArrayList<>();
    private List<WorkflowOut> neededOutputs = new ArrayList<>();

    public WorkflowStep() {
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

    public List<WorkflowIn> getInnerUnmatchedInputs() {
        return innerUnmatchedInputs;
    }

    public void setInnerUnmatchedInputs(List<WorkflowIn> innerUnmatchedInputs) {
        this.innerUnmatchedInputs = innerUnmatchedInputs;
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
}
