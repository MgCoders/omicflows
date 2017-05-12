package com.mgcoders.api;

import com.mgcoders.cwl.CwlOps;
import com.mgcoders.db.MongoClientProvider;
import com.mgcoders.db.Tool;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 02/05/17.
 */
@Path("/tools")
public class ToolsService {

    @Inject
    MongoClientProvider mongoClientProvider;
    @Inject
    CwlOps cwlOps;

    @GET
    @Path("/{query}")
    public List<Tool> getQuery(@PathParam("query") String query) {
        System.out.println("HOLA GET");
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @GET
    @JWTTokenNeeded
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> get() {
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @POST
    @JWTTokenNeeded
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newTool(Tool tool) {
        try {
            tool.generateJson();
            if (cwlOps.isValidCwlTool(tool.getJson())) {
                mongoClientProvider.getToolCollection().insertOne(tool);
                return Response.status(Response.Status.ACCEPTED).build();
            }
        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid cwl").build();
    }


}
