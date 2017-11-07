package coop.magnesium.db.entities;

import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 07/11/17.
 */
@Entity
public class Job {

    @Id
    private String id;
    private String userId;
    private String workflowId;
    private Workflow workflow;
    private List<JobResource> resources = new ArrayList<>();
    private boolean ready = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public List<JobResource> getResources() {
        return resources;
    }

    public void setResources(List<JobResource> resources) {
        this.resources = resources;
    }
}
