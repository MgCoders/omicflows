package coop.magnesium.api;

import coop.magnesium.api.utils.JWTTokenNeeded;
import coop.magnesium.api.utils.RoleNeeded;
import coop.magnesium.db.MongoClientProvider;
import coop.magnesium.db.entities.*;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static coop.magnesium.utils.RestUtils.getFieldContent;
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
    @ApiOperation(value = "Add file resource to Job", response = Job.class)
    public Response addFileResourceToJob(@PathParam("jobId") String jobId, MultipartFormDataInput multipartFormDataInput) {
        try {
            Job job = mongoClientProvider.getJobsCollection().find(and(eq("_id", jobId))).first();
            if (job == null) throw new ObjectNotFoundException("Job not found");
            Map<String, List<InputPart>> uploadForm = multipartFormDataInput.getFormDataMap();
            //Busco Archivo
            for (String inputName : job.getWorkflow().getNeededInputs().stream().map(WorkflowIn::getName).collect(Collectors.toList())) {
                List<InputPart> inputParts = uploadForm.getOrDefault(inputName, new ArrayList<>());
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
                    resource.setName(inputName);
                    resource.setValue(resourceUrl);
                    job.getResources().add(resource);
                }
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

    @POST
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add simple resource to Job", response = Job.class)
    public Response addSimpleResourceToJob(@PathParam("jobId") String jobId, JobResource jobResource) {
        try {
            Job job = mongoClientProvider.getJobsCollection().find(and(eq("_id", jobId))).first();
            if (job.getResources().stream().filter(jr -> jr.getName().equals(jobResource.getName())).count() > 0)
                throw new ObjectExistsException("Alredy exists " + jobResource.getName());
            job.getResources().add(jobResource);
            mongoClientProvider.getJobsCollection().replaceOne(eq("_id", jobId), job);
            return Response.status(Response.Status.OK).entity(job).build();
        } catch (ObjectExistsException e) {
            logger.warning(e.getMessage());
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @JWTTokenNeeded
    @RoleNeeded({Role.USER, Role.ADMIN})
    @Path("/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Close Job", response = Job.class)
    public Response closeJob(@PathParam("jobId") String jobId) {
        try {
            Job job = mongoClientProvider.getJobsCollection().find(and(eq("_id", jobId))).first();
            if (job.isReady()) return Response.status(Response.Status.OK).entity(job).build();
            Boolean allInputsOk = job.getWorkflow().getNeededInputs().stream().allMatch(workflowIn -> job.getResources().stream().anyMatch(jobResource -> workflowIn.getName().equals(jobResource.getName())));
            if (allInputsOk && job.getWorkflow().getNeededInputs().size() == job.getResources().size()) {
                String descriptorFileUrl = storageProviderS3.put(job.getWorkflow().getName() + "-job.yml", job.getUserId(), generateInputDescriptorFile(job.getResources()));
                if (descriptorFileUrl == null) throw new ObjectNotFoundException("S3 path not found");
                job.setInputsFilePath(descriptorFileUrl);
                String jsonWorkflowFileUrl = storageProviderS3.put(job.getWorkflow().getName() + ".json", job.getUserId(), generateWorkflowFile(job.getWorkflow().getJson()));
                if (jsonWorkflowFileUrl == null) throw new ObjectNotFoundException("S3 path not found");
                job.setJsonWorkflowFilePath(jsonWorkflowFileUrl);
                String yamlWorkflowFilePath = storageProviderS3.put(job.getWorkflow().getName() + ".cwl", job.getUserId(), generateWorkflowFile(job.getWorkflow().getCwl()));
                if (yamlWorkflowFilePath == null) throw new ObjectNotFoundException("S3 path not found");
                job.setYamlWorkflowFilePath(yamlWorkflowFilePath);
                job.setReady(true);
                mongoClientProvider.getJobsCollection().replaceOne(eq("_id", jobId), job);
                return Response.status(Response.Status.OK).entity(job).build();
            }
            return Response.status(Response.Status.CONFLICT).entity("Error con inputs").build();
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private InputStream generateInputDescriptorFile(List<JobResource> jobResourceList) throws UnsupportedEncodingException, MalformedURLException {
        final StringBuilder stringBuilder = new StringBuilder();
        jobResourceList.forEach(jobResource -> {
            stringBuilder.append(jobResource.getName()).append(": ");
            if (jobResource.get_class().toLowerCase().equals("file")) {
                stringBuilder.append(System.lineSeparator());
                stringBuilder.append("\t").append("class").append(": ").append("File");
                stringBuilder.append(System.lineSeparator());
                String fileName = jobResource.getValue();
                try {
                    URL url = new URL(jobResource.getValue());
                    fileName = url.getPath();
                } catch (MalformedURLException e) {
                }
                stringBuilder.append("\t").append("path").append(": ").append(fileName);
            } else {
                stringBuilder.append(jobResource.getValue());
            }
            stringBuilder.append(System.lineSeparator());
        });
        return new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8.name()));
    }

    private InputStream generateWorkflowFile(String string) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8.name()));
    }


}
