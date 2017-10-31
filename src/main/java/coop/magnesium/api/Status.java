package coop.magnesium.api;

import coop.magnesium.utils.PropertiesFromFile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by rsperoni on 14/09/17.
 */
@Api(description = "Status", tags = "status")
@Path("/status")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class Status {

    @Inject
    @PropertiesFromFile
    Properties endpointsProperties;

    @Inject
    String jbossNodeName;


    @GET
    @Produces({MediaType.TEXT_HTML})
    @ApiOperation(value = "Get system status", notes = "html", response = String.class)
    public String status() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">");
        sb.append("<span>VERSION=" + endpointsProperties.getProperty("project.version") + "</span></br>");
        sb.append("<span>NODO=" + jbossNodeName + "</span></br>");
        sb.append("<span>LOGS=" + "<a href=''>Ver logs</a></span></br>");
        sb.append("<span><a href=http://" + endpointsProperties.getProperty("rest.api.host") + "/" + endpointsProperties.getProperty("rest.api.path") + "/swagger.json>Swagger</a></span></br>");
        sb.append("</pre>");
        sb.append("</html>");
        return sb.toString();
    }


}
