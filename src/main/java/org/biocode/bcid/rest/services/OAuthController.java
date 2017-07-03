package org.biocode.bcid.rest.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.biocode.bcid.models.Client;
import org.biocode.bcid.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Controller
@Path("/oAuth2")
public class OAuthController {

    private final ClientService clientService;

    @Autowired
    public OAuthController(ClientService clientService) {
        this.clientService = clientService;
    }

    @POST
    @Path("token")
    @Produces(MediaType.APPLICATION_JSON)
    public AccessToken generateToken(@FormParam("grant_type") String grantType,
                                     @FormParam("client_id") String id,
                                     @FormParam("client_secret") String secret) {
        if (grantType == null || !grantType.equals("client_credentials")) {
            throw new BadRequestException("invalid grant_type. only client_credentials is valid");
        }

        Client client = clientService.getClient(id, secret);

        if (client == null) {
            throw new BadRequestException("invalid client_id and/or client_secret");
        }

        return new AccessToken(clientService.generateToken(client));
    }

//    @GET
//    @Path("revoke")
//    @Produces(MediaType.APPLICATION_JSON)
//    public AcknowledgedResponse revokeToken() {
//        client.revokeToken();
//    }

    private static class AccessToken {
        @JsonProperty("token_type")
        public String tokenType = "bearer";
        @JsonProperty("expires_in")
        public int expiresIn = ClientService.TOKEN_EXPIRATION;
        @JsonProperty("access_token")
        public String accessToken;

        public AccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

}
