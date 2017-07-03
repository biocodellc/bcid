package org.biocode.bcid.rest.services;

import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.models.Bcid;
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
    private final BcidProperties props;

    @Autowired
    public BcidController(BcidService bcidService, BcidProperties props) {
        this.bcidService = bcidService;
        this.props = props;
    }

    @GET
    @Path("{identifier: .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Bcid get(@PathParam("identifier") URI identifier) {
        return bcidService.getBcid(identifier);
    }

    @Authenticated
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bcid create(Bcid bcid) {

        if (bcid == null) {
            throw new BadRequestException("bcid must not be null");
        }

        // we can override ezid requests
        if (!props.ezidRequest()) {
            bcid.setEzidRequest(false);
        }

        bcid.setClient(clientContext.client());
        return bcidService.create(bcid);
    }

    @Authenticated
    @PUT
    @Path("{identifier: .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bcid bcidUpdate(@PathParam("identifier") URI identifier,
                           Bcid bcid) {

        Bcid existingBcid = bcidService.getBcid(identifier);

        if (existingBcid == null) {
            throw new BadRequestException("bcid not found with the given identifier");
        }

        if (!existingBcid.identifier().equals(identifier)) {
            throw new BadRequestException("the identifier does not match the bcid object identifier. " +
                    "You can not update the bcid identifier");
        }

        if (!clientContext.client().equals(existingBcid.client())) {
            throw new ServerErrorException("You are not authorized to edit this bcid", 403);
        }

        existingBcid.update(bcid);
        bcidService.update(existingBcid);

        return existingBcid;
    }
}
