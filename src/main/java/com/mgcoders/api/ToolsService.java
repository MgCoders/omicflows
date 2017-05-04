package com.mgcoders.api;

import com.mgcoders.entities.MongoClientProvider;
import com.mgcoders.entities.Tool;

import javax.ejb.EJB;
import javax.enterprise.inject.*;
import javax.ws.rs.*;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 02/05/17.
 */
@Path("/tools")
public class ToolsService {

    @EJB
    MongoClientProvider mongoClientProvider;

    @GET
    @Path("/{query}")
    public List<Tool> getQuery(@PathParam("query") String query) {
        System.out.println("HOLA GET");
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> get() {
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void newTool(Tool tool){
        mongoClientProvider.getToolCollection().insertOne(tool);
    }


}
