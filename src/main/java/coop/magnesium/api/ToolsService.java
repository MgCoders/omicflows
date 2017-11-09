package coop.magnesium.api;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import coop.magnesium.api.utils.JWTTokenNeeded;
import coop.magnesium.api.utils.RoleNeeded;
import coop.magnesium.cwl.CwlOps;
import coop.magnesium.db.MongoClientProvider;
import coop.magnesium.db.entities.Role;
import coop.magnesium.db.entities.Tool;
import coop.magnesium.utils.Logged;
import coop.magnesium.utils.ex.ObjectNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.bson.BsonString;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static coop.magnesium.utils.RestUtils.getFieldContent;

/**
 * Created by rsperoni on 02/05/17.
 */
@Path("/tools")
@Produces(MediaType.APPLICATION_JSON)
@Api(description = "Tools", tags = "tools")
@Logged
public class ToolsService {

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
    @ApiOperation(value = "List tools", response = Tool.class, responseContainer = "List")
    public List<Tool> get() {
        return mongoClientProvider.getToolCollection().find(Tool.class).into(new ArrayList<>());
    }

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/new")
    @Consumes("multipart/form-data")
    @ApiOperation(value = "New tool from file", response = Tool.class)
    public Response newTool(MultipartFormDataInput multipartFormDataInput) {
        try {
            Map<String, List<InputPart>> uploadForm = multipartFormDataInput.getFormDataMap();
            //Busco Archivo
            List<InputPart> inputParts = uploadForm.getOrDefault("file", new ArrayList<>());
            for (InputPart inputPart : inputParts) {
                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String fieldName = getFieldContent(headers, "filename");
                if (fieldName == null) throw new ObjectNotFoundException("Filename not found");
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                Tool tool = new Tool();
                tool.setName(fieldName);
                tool.setCwl(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
                tool.generateJson();
                if (cwlOps.isValidCwlTool(tool)) {
                    mongoClientProvider.getToolCollection().insertOne(tool);
                    return Response.status(Response.Status.ACCEPTED).entity(tool).build();
                }
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid cwl").build();
        } catch (ObjectNotFoundException notFound) {
            logger.warning(notFound.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(notFound.getMessage()).build();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "New tool", response = Tool.class)
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
    @JWTTokenNeeded
    @RoleNeeded({Role.ADMIN})
    @Path("/delete/{toolId}")
    @ApiOperation(value = "Delete tool", response = Response.class)
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
