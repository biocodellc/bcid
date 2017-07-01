package org.biocode.bcid.rest.services;

import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.service.BcidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Controller
@Path("/")
public class BcidController {

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

        //TODO need to authenticate fims instances
        return bcidService.create(bcid);
    }

    @PUT
    @Path("{identifier: .+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Bcid bcidUpdate(@PathParam("identifier") URI identifier,
                           Bcid bcid) {

        //TODO need to authenticate fims instances
        Bcid existingBcid = bcidService.getBcid(identifier);

        if (existingBcid == null) {
            throw new BadRequestException("bcid not found with the given identifier");
        }

        if (!existingBcid.identifier().equals(identifier)) {
            throw new BadRequestException("the identifier does not match the bcid object identifier. " +
                    "You can not update the bcid identifier");
        }

        existingBcid.update(bcid);
        bcidService.update(existingBcid);

        return existingBcid;
    }
}
