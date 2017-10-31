package coop.magnesium.api.aux;

import coop.magnesium.utils.PropertiesFromFile;
import io.swagger.jaxrs.config.BeanConfig;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Properties;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing
 * JAX-RS resources in your application.
 *
 * @author airhacks.com
 */
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {

    @Inject
    @PropertiesFromFile
    Properties endpointsProperties;

    /**
     * Add swagger configuraction
     */
    @PostConstruct
    public void init() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(endpointsProperties.getProperty("project.version"));
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost(endpointsProperties.getProperty("rest.api.host"));
        beanConfig.setBasePath(endpointsProperties.getProperty("rest.api.path"));
        beanConfig.setResourcePackage("coop.magnesium.api");
        beanConfig.setDescription("Sulfur");
        beanConfig.setTitle("Omicflows backend");
        beanConfig.setContact("rsperoni@mgcoders.com");
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
    }
}
