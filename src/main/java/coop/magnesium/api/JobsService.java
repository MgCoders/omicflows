package coop.magnesium.api;

import coop.magnesium.api.utils.JWTTokenNeeded;
import coop.magnesium.api.utils.RoleNeeded;
import coop.magnesium.db.MongoClientProvider;
import coop.magnesium.db.entities.Job;
import coop.magnesium.db.entities.JobResource;
import coop.magnesium.db.entities.Role;
import coop.magnesium.db.entities.Workflow;
import coop.magnesium.utils.StorageProviderS3;
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
    private static String getFileName(MultivaluedMap<String, String> headers, String fileNameField) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith(fileNameField))) {

                String[] name = filename.split("=");

                String finalFileName = sanitizeFilename(name[1]);
                return finalFileName;
            }
        }
        return null;
    }

    private static String sanitizeFilename(String s) {
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
    public Response addResourceToJob(@PathParam("jobId") String jobId, MultipartFormDataInput multipartFormDataInput) {
        final String UPLOADED_FILE_PARAMETER_NAME = "file";
        final String UPLOADED_FILE_NAME_PARAMETER_NAME = "filename";
        try {
            Job job = mongoClientProvider.getJobsCollection().find(and(eq("_id", jobId))).first();
            if (job == null) throw new ObjectNotFoundException("Job not found");
            Map<String, List<InputPart>> uploadForm = multipartFormDataInput.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);
            if (inputParts.size() == 0) throw new ObjectNotFoundException("Uploaded file not found");
            for (InputPart inputPart : inputParts) {
                MultivaluedMap<String, String> headers = inputPart.getHeaders();
                String filename = getFileName(headers, UPLOADED_FILE_NAME_PARAMETER_NAME);
                if (filename == null) throw new ObjectNotFoundException("Filename not found");
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                String resourceUrl = storageProviderS3.put(filename, job.getUserId(), inputStream);
                if (resourceUrl == null) throw new ObjectNotFoundException("S3 path not found");
                JobResource resource = new JobResource();
                resource.set_class(filename);
                resource.setName(filename);
                resource.setPath(resourceUrl);
                job.getResources().add(resource);
                mongoClientProvider.getJobsCollection().replaceOne(eq("_id", jobId), job);
                return Response.status(Response.Status.OK).entity(job).build();
            }
        } catch (ObjectNotFoundException notFound) {
            logger.warning(notFound.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(notFound.getMessage()).build();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid parameters").build();
    }
}
