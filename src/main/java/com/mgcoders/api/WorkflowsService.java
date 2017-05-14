package com.mgcoders.api;

import com.mgcoders.cwl.CwlOps;
import com.mgcoders.db.MongoClientProvider;
import com.mgcoders.db.entities.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 02/05/17.
 */
@Path("/workflows")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class WorkflowsService {

    @Inject
    MongoClientProvider mongoClientProvider;
    @Inject
    CwlOps cwlOps;


    @GET
    //@JWTTokenNeeded
    @Produces(MediaType.APPLICATION_JSON)
    public List<Workflow> get() {
        return mongoClientProvider.getWorkflowCollection().find(Workflow.class).into(new ArrayList<>());
    }

    @POST
    //@JWTTokenNeeded
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newWorkflow(User user) {
        try {
            Workflow workflow = cwlOps.createWorkflow("YourNewWorkflow", user.getId());
            mongoClientProvider.getWorkflowCollection().insertOne(workflow);
            return Response.status(Response.Status.ACCEPTED).entity(workflow).build();
        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid workflow").build();
    }

    @POST
    //@JWTTokenNeeded
    @Path("/step/{toolId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createWorkflowStep(@PathParam("toolId") String toolId, List<WorkflowIn> workflowInList) {
        try {
            Tool tool = mongoClientProvider.getToolCollection().find(eq("_id", toolId)).first();
            if (tool != null) {
                WorkflowStep workflowStep = cwlOps.createWorkflowStep(tool, workflowInList);
                return Response.status(Response.Status.FOUND).entity(workflowStep).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameters").build();
    }

    @POST
    //@JWTTokenNeeded
    @Path("/{workflowId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStepToWorkflow(@PathParam("workflowId") String workflowId, WorkflowStep workflowStep) {
        try {
            Workflow workflow = mongoClientProvider.getWorkflowCollection().find(and(eq("_id", workflowId), eq("complete", false))).first();
            if (workflowId != null) {
                workflow = cwlOps.addStepToWorkflow(workflow, workflowStep);
                mongoClientProvider.getWorkflowCollection().replaceOne(eq("_id", workflowId), workflow);
                return Response.status(Response.Status.OK).entity(workflow).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameters").build();
    }

    @POST
    //@JWTTokenNeeded
    @Path("/close/{workflowId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response closeWorkflow(@PathParam("workflowId") String workflowId) {
        try {
            Workflow workflow = mongoClientProvider.getWorkflowCollection().find(and(eq("_id", workflowId), eq("complete", false))).first();
            if (workflowId != null) {
                workflow = cwlOps.postProcessWorkflow(workflow);
                mongoClientProvider.getWorkflowCollection().replaceOne(eq("_id", workflowId), workflow);
                return Response.status(Response.Status.OK).entity(workflow).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameters").build();
    }


}
