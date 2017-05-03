package com.mgcoders.api;

import com.mgcoders.entities.MongoClientProvider;
import com.mgcoders.entities.Tool;

import javax.ejb.EJB;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsperoni on 02/05/17.
 */
@Path("/tools")
@Produces("application/json")
@Consumes("application/json")
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
    public List<Tool> get() {
        System.out.println("HOLA GET");
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }


}
