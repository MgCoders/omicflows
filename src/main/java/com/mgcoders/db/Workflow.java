package com.mgcoders.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 11/05/17.
 */
public class Workflow {

    private String name;
    private String cwl;
    private String json;
    private Boolean complete;

    private List<WorkflowStep> steps = new ArrayList<>();

    private List<WorkflowIn> neededInputs = new ArrayList<>();
    private List<WorkflowOut> neededOutputs = new ArrayList<>();


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

    public List<WorkflowIn> getNeededInputs() {
        return neededInputs;
    }

    public List<WorkflowOut> getNeededOutputs() {
        return neededOutputs;
    }
}