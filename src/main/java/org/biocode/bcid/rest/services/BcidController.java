package org.biocode.bcid.rest.services;

import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.repositories.ClientRepository;
import org.biocode.bcid.rest.Authenticated;
import org.biocode.bcid.rest.ClientContext;
import org.biocode.bcid.service.BcidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Controller
@Path("/")
public class BcidController {
    @Autowired
    private ClientContext clientContext;

    private final BcidService bcidService;
    private final ClientRepository clientRepository;

    @Autowired
    public BcidController(BcidService bcidService, ClientRepository clientRepository) {
        this.bcidService = bcidService;
        this.clientRepository = clientRepository;
    }

    @Authenticated
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bcid create(Bcid bcid) {

        if (bcid == null) {
            throw new BadRequestException("bcid must not be null");
        }

        return bcidService.create(bcid, clientContext.client().id());
    }

    @Authenticated
    @PUT
    @Path("{identifier: .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bcid update(@PathParam("identifier") URI identifier,
                       Bcid bcid) {

        if (!clientRepository.isAssociated(clientContext.client().id(), identifier)) {
            throw new BadRequestException("Either a bcid with the given identifier doesn't exist or the client doesn't own the identifier");

        }

        bcid.setIdentifier(identifier);

        bcidService.update(bcid);

        return bcid;
    }
}
