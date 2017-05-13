package com.mgcoders.db.entities;

import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 11/05/17.
 */
@Entity
public class Workflow {

    @Id
    private String id;
    private String userId;
    private String name;
    private String cwl;
    private String json;
    private Boolean complete = false;
    private List<WorkflowStep> steps = new ArrayList<>();
    private List<WorkflowIn> neededInputs = new ArrayList<>();
    private List<WorkflowOut> neededOutputs = new ArrayList<>();

    public Workflow() {
    }

    public Workflow(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
