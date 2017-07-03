package org.biocode.bcid.rest;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Custom filter for checking if a client is logged in
 */
@Provider
@Authenticated
@Priority(Priorities.AUTHORIZATION)
public class AuthenticatedFilter implements ContainerRequestFilter {
    @Autowired
    private ClientContext clientContext;

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        if (clientContext.client() == null) {
            throw new ForbiddenException("You must be logged in to access this service");
        }
    }
}
