package com.mgcoders.api;

import com.mgcoders.cwl.CwlOps;
import com.mgcoders.db.MongoClientProvider;
import com.mgcoders.db.entities.User;
import com.mgcoders.db.entities.Workflow;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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
            workflow = mongoClientProvider.getWorkflowCollection().find(Workflow.class).first();
            return Response.status(Response.Status.ACCEPTED).entity(workflow).build();
        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid workflow").build();
    }


}
