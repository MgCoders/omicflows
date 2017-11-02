package coop.magnesium.api;

import coop.magnesium.api.aux.JWTTokenNeeded;
import coop.magnesium.api.aux.RoleNeeded;
import coop.magnesium.cwl.CwlOps;
import coop.magnesium.db.MongoClientProvider;
import coop.magnesium.db.entities.*;
import coop.magnesium.utils.Logged;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 02/05/17.
 */
@Api(description = "Workflows", tags = "workflows")
@Path("/workflows")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Logged
public class WorkflowsService {

    @Inject
    MongoClientProvider mongoClientProvider;
    @Inject
    CwlOps cwlOps;
    @Inject
    private Logger logger;


    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "List workflows", response = Workflow.class, responseContainer = "List")
    public List<Workflow> get() {
        return mongoClientProvider.getWorkflowCollection().find(Workflow.class).into(new ArrayList<>());
    }

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "New workflow", response = Workflow.class)
    public Response newWorkflow(User user) {
        try {
            Workflow workflow = cwlOps.createWorkflow("YourNewWorkflow", user.getId());
            mongoClientProvider.getWorkflowCollection().insertOne(workflow);
            return Response.status(Response.Status.ACCEPTED).entity(workflow).build();
        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid workflow").build();
    }

    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/step/{toolId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create workflow step", response = WorkflowStep.class)
    public Response createWorkflowStep(@PathParam("toolId") String toolId) {
        try {
            Tool tool = mongoClientProvider.getToolCollection().find(eq("_id", toolId)).first();
            if (tool != null) {
                WorkflowStep workflowStep = cwlOps.createWorkflowStep(tool);
                return Response.status(Response.Status.ACCEPTED).entity(workflowStep).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameters").build();
    }

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/{workflowId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add workflow step to Workflow", response = Workflow.class)
    public Response addStepToWorkflow(@PathParam("workflowId") String workflowId, WorkflowStep workflowStep) {
        logger.info(workflowStep.toString());
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
            ignored.printStackTrace();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameters").build();
    }

    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/close/{workflowId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Close workflow", response = Workflow.class)
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
