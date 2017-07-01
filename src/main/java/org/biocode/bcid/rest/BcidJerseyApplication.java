package org.biocode.bcid.rest;

import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author rjewing
 */
public class BcidJerseyApplication extends ResourceConfig {

    public BcidJerseyApplication() {
        super();

        packages("org.biocode.bcid.rest.services");
        register(BcidObjectMapper.class);
        register(JacksonFeature.class);

//        register(FimsExceptionMapper.class);

//        register(RequestContextFilter.class);
//        register(AuthenticatedFilter.class);
//        register(AdminFilter.class);
//        register(AuthenticationFilter.class);
//        register(AuthenticatedUserResourceFilter.class);
//        register(RequestLoggingFilter.class);

        // need to manually register all subResources. This is so they get registered with the SpringComponentProvider
        // otherwise, the VersionTransformer advice will not register with the subResource method
//        register(ProjectsResource.class);
    }
}
