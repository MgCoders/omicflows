package com.mgcoders.api;

import com.mgcoders.cwl.CwlOps;
import com.mgcoders.db.MongoClientProvider;
import com.mgcoders.db.entities.Tool;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonString;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by rsperoni on 02/05/17.
 */
@Path("/tools")
@Produces(MediaType.APPLICATION_JSON)
public class ToolsService {

    @Inject
    MongoClientProvider mongoClientProvider;
    @Inject
    CwlOps cwlOps;
    @Inject
    private Logger logger;

    @GET
    @Path("/{query}")
    public List<Tool> getQuery(@PathParam("query") String query) {
        System.out.println("HOLA GET");
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @GET
    //@JWTTokenNeeded
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> get() {
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @POST
    //@JWTTokenNeeded
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveTool(Tool tool) {
        try {
            tool.generateJson();
            if (cwlOps.isValidCwlTool(tool)) {
                if (tool.getId() == null) {
                    mongoClientProvider.getToolCollection().insertOne(tool);
                    return Response.status(Response.Status.ACCEPTED).entity(tool).build();
                } else {
                    UpdateResult updateResult = mongoClientProvider.getToolCollection().replaceOne(eq("_id", new BsonString(tool.getId())), tool);
                    logger.info(updateResult.toString());
                    if (updateResult.getModifiedCount() > 0) {
                        return Response.status(Response.Status.ACCEPTED).entity(tool).build();
                    } else {
                        return Response.status(Response.Status.NOT_MODIFIED).build();
                    }
                }

            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid cwl").build();
    }

    @DELETE
    //@JWTTokenNeeded
    @Path("/delete/{toolId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTool(@PathParam("toolId") String toolId) {
        try {

            DeleteResult deleteResult = mongoClientProvider.getToolCollection().deleteOne(eq("_id", new BsonString(toolId)));
            logger.info(deleteResult.toString());
            if (deleteResult.getDeletedCount() > 0) {
                return Response.status(Response.Status.ACCEPTED).build();
            } else {
                return Response.status(Response.Status.NOT_MODIFIED).build();
            }


        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


}
