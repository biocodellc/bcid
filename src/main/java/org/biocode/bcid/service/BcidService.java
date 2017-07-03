package org.biocode.bcid.service;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.Encoder;
import org.biocode.bcid.ezid.EzidException;
import org.biocode.bcid.ezid.EzidUtils;
import org.biocode.bcid.ezid.IEzidService;
import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.repositories.BcidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Service class for handling {@link Bcid} persistence
 */
@Service
public class BcidService {
    private static final String scheme = "ark:";
    private static final Logger logger = LoggerFactory.getLogger(BcidService.class);

    private final BcidRepository bcidRepository;
    private final BcidProperties props;
    private final Encoder encoder;
    private final IEzidService ezidService;

    @Autowired
    public BcidService(BcidRepository bcidRepository, BcidProperties props, Encoder encoder, IEzidService ezidService) {
        this.bcidRepository = bcidRepository;
        this.props = props;
        this.encoder = encoder;
        this.ezidService = ezidService;
    }

    @Transactional
    public Bcid create(Bcid bcid) {
        int naan = props.naan();

        if (!props.ezidRequest()) {
            bcid.setEzidRequest(false);
        }
        bcidRepository.save(bcid);

        // generate the identifier
        URI identifier = generateBcidIdentifier(bcid.id(), naan);
        bcid.setIdentifier(identifier);

        if (bcid.webAddress() != null && bcid.webAddress().toString().contains("%7Bark%7D")) {
            bcid.setWebAddress(URI.create(StringUtils.replace(
                    bcid.webAddress().toString(),
                    "%7Bark%7D",
                    identifier.toString()
            )));
        }

        bcidRepository.save(bcid);
        if (bcid.ezidRequest()) {
            createBcidsEZIDs();
        }

        return bcid;

    }

    public void update(Bcid bcid) {
        bcidRepository.save(bcid);
    }

    @Transactional(readOnly = true)
    public Bcid getBcid(URI identifier) {
        return bcidRepository.findOneByIdentifier(identifier);
    }


    private URI generateBcidIdentifier(int bcidId, int naan) {
        String bow = scheme + "/" + naan + "/";

        // Create the shoulder Bcid (String Bcid Bcid)
        String shoulder = encoder.encode(new BigInteger(String.valueOf(bcidId)));

        // Create the identifier
        return URI.create(bow + shoulder);
    }

    @Transactional(readOnly = true)
    private Set<Bcid> getBcidsWithEzidRequestNotMade() {
        return bcidRepository.findAllByEzidRequestTrueAndEzidMadeFalse();
    }

    /**
     * Go through bcids table and create any ezidService fields that have yet to be created. We want to create any
     * EZIDs that have not been made yet.
     * <p/>
     * case
     */
    private void createBcidsEZIDs() {
        // NOTE: On any type of EZID error, we DON'T want to fail the process.. This means we need
        // a separate mechanism on the server side to check creation of EZIDs.  This is easy enough to do
        // in the Database.
        HashMap<String, String> ezidErrors = new HashMap<>();
        // Setup EZID account/login information
        try {
            ezidService.login(props.ezidUser(), props.ezidPass());
        } catch (EzidException e) {
            ezidErrors.put(null, ExceptionUtils.getStackTrace(e));

        }
        Set<Bcid> bcids = getBcidsWithEzidRequestNotMade();
        EzidUtils ezidUtils = new EzidUtils(props);

        for (Bcid bcid : bcids) {
            // Dublin Core metadata profile element
            HashMap<String, String> map = ezidUtils.getDcMap(bcid);

            // Register this as an EZID
            try {
                URI identifier = new URI(ezidService.createIdentifier(String.valueOf(bcid.identifier()), map));
                bcid.setEzidMade(true);
                logger.info("{}", identifier.toString());
            } catch (EzidException e) {
                logger.info("EzidException thrown trying to create Ezid {}. Trying to update now.", bcid.identifier(), e);
                // Attempt to set Metadata if this is an Exception
                try {
                    ezidService.setMetadata(String.valueOf(bcid.identifier()), map);
                    bcid.setEzidMade(true);
                } catch (EzidException e1) {
                    logger.error("Exception thrown in attempting to create OR update EZID {}, a permission issue?", bcid.identifier(), e1);
                    ezidErrors.put(String.valueOf(bcid.identifier()), ExceptionUtils.getStackTrace(e1));
                }

            } catch (URISyntaxException e) {
                logger.error("Bad uri syntax for " + bcid.identifier() + ", " + map, e);
                ezidErrors.put(String.valueOf(bcid.identifier()), "Bad uri syntax");
            }
        }

        if (!ezidErrors.isEmpty()) {
            ezidUtils.sendErrorEmail(ezidErrors);
        }
    }

    public List<Bcid> getBcidsWithOutEzidRequest() {
        return bcidRepository.findAllByEzidRequestFalse();
    }
}
