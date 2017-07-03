package org.biocode.bcid.rest;

import org.biocode.bcid.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Custom filter to set the {@link ClientContext#client} for the incoming request
 *
 * @author RJ Ewing
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest webRequest;
    @Autowired
    private ClientContext clientContext;

    @Autowired
    private ClientService clientService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

        // If check if the HTTP Authorization header is present and contains a Bearer accessToken
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token from the HTTP Authorization header
            String accessToken = authHeader.substring("Bearer".length()).trim();

            if (!accessToken.isEmpty()) {
                clientContext.setClient(clientService.getClient(accessToken));
            }
        }
    }
}
