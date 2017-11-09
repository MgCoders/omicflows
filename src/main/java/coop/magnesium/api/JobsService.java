package coop.magnesium.api;

import coop.magnesium.api.utils.JWTTokenNeeded;
import coop.magnesium.api.utils.RoleNeeded;
import coop.magnesium.db.MongoClientProvider;
import coop.magnesium.db.entities.Job;
import coop.magnesium.db.entities.JobResource;
import coop.magnesium.db.entities.Role;
import coop.magnesium.db.entities.Workflow;
import coop.magnesium.utils.StorageProviderS3;
import coop.magnesium.utils.ex.ObjectExistsException;
import coop.magnesium.utils.ex.ObjectNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 07/11/17.
 */
@Api(description = "Jobs", tags = "jobs")
@Path("/jobs")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class JobsService {

    @Inject
    MongoClientProvider mongoClientProvider;

    @Inject
    private Logger logger;

    @Inject
    private StorageProviderS3 storageProviderS3;

    /**
     * Extract filename from HTTP heaeders.
     *
     * @param headers
     * @return
     */
    private static String getFieldContent(MultivaluedMap<String, String> headers, String fieldName) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String field : contentDisposition) {
            if ((field.trim().startsWith(fieldName))) {
                String[] name = field.split("=");
                return sanitizeFieldContent(name[1]);
            }
        }
        return null;
    }

    /**
     * Extract contentType from HTTP heaeders.
     *
     * @param headers
     * @return
     */
    private static String getContentType(MultivaluedMap<String, String> headers) {
        String contentType = headers.getFirst("Content-Type");
        return sanitizeFieldContent(contentType);
    }

    private static String sanitizeFieldContent(String s) {
        return s.trim().replaceAll("\"", "");
    }

    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "List jobs", response = Job.class, responseContainer = "List")
    public List<Job> get() {
        return mongoClientProvider.getJobsCollection().find(Job.class).into(new ArrayList<>());
    }

    @GET
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/{workflowId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get jobs by workflow", response = Job.class, responseContainer = "List")
    public List<Job> getByWorkflow(@PathParam("workflowId") String workflowId) {
        return mongoClientProvider.getJobsCollection().find(eq("workflowId", workflowId)).into(new ArrayList<>());
    }

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "New job", response = Job.class)
    public Response newJob(Workflow workflow) {
        try {
            Job job = new Job();
            job.setUserId(workflow.getUserId());
            job.setWorkflowId(workflow.getId());
            job.setWorkflow(mongoClientProvider.getWorkflowCollection().find(eq("_id", workflow.getId())).first());
            mongoClientProvider.getJobsCollection().insertOne(job);
            return Response.status(Response.Status.ACCEPTED).entity(job).build();
        } catch (Exception ignored) {
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid job").build();
    }

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/{jobId}")
    @Consumes("multipart/form-data")
    @ApiOperation(value = "Add resource to Job", response = Workflow.class)
    public Response addFileResourceToJob(@PathParam("jobId") String jobId, MultipartFormDataInput multipartFormDataInput) {
        try {
            Job job = mongoClientProvider.getJobsCollection().find(and(eq("_id", jobId))).first();
            if (job == null) throw new ObjectNotFoundException("Job not found");
            Map<String, List<InputPart>> uploadForm = multipartFormDataInput.getFormDataMap();
            //Busco Archivo
            List<InputPart> inputParts = uploadForm.getOrDefault("file", new ArrayList<>());
            for (InputPart inputPart : inputParts) {
                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String fieldName = getFieldContent(headers, "filename");
                if (fieldName == null) throw new ObjectNotFoundException("Filename not found");
                if (job.getResources().stream().filter(jobResource -> jobResource.getName().equals(fieldName)).count() > 0)
                    throw new ObjectExistsException("Alredy exists " + fieldName);
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                String resourceUrl = storageProviderS3.put(fieldName, job.getUserId(), inputStream);
                if (resourceUrl == null) throw new ObjectNotFoundException("S3 path not found");
                JobResource resource = new JobResource();
                resource.set_class("file");
                resource.setName(fieldName);
                resource.setPath(resourceUrl);
                job.getResources().add(resource);
            }
            mongoClientProvider.getJobsCollection().replaceOne(eq("_id", jobId), job);
            return Response.status(Response.Status.OK).entity(job).build();
        } catch (ObjectNotFoundException notFound) {
            logger.warning(notFound.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(notFound.getMessage()).build();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } catch (ObjectExistsException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }


}
