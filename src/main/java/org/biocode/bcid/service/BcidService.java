package org.biocode.bcid.service;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.Encoder;
import org.biocode.bcid.ezid.EzidException;
import org.biocode.bcid.ezid.EzidRequestType;
import org.biocode.bcid.ezid.EzidUtils;
import org.biocode.bcid.ezid.IEzidService;
import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.repositories.ClientRepository;
import org.biocode.bcid.repositories.EzidQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * Service class for handling {@link Bcid} persistence
 */
@Service
public class BcidService {
    private static final String scheme = "ark:";
    private static final Logger logger = LoggerFactory.getLogger(BcidService.class);

    private final EzidQueue ezidQueue;
    private final ClientRepository clientRepository;
    private final BcidProperties props;
    private final Encoder encoder;
    private final IEzidService ezidService;

    @Autowired
    public BcidService(EzidQueue ezidQueue, ClientRepository clientRepository, BcidProperties props,
                       Encoder encoder, IEzidService ezidService) {
        this.ezidQueue = ezidQueue;
        this.clientRepository = clientRepository;
        this.props = props;
        this.encoder = encoder;
        this.ezidService = ezidService;
    }

    @Transactional
    public Bcid create(Bcid bcid, String clientId) {
        // generate the identifier
        URI identifier = generateBcidIdentifier(ezidQueue.incrementId());
        bcid.setIdentifier(identifier);

        if (bcid.webAddress() != null && bcid.webAddress().toString().contains("%7Bark%7D")) {
            bcid.setWebAddress(URI.create(StringUtils.replace(
                    bcid.webAddress().toString(),
                    "%7Bark%7D",
                    identifier.toString()
            )));
        }

        bcid.setRequestType(EzidRequestType.MINT);

        ezidQueue.save(bcid);
        clientRepository.associateIdentifier(clientId, bcid.identifier());

        return bcid;

    }

    public void update(Bcid bcid) {
        bcid.setRequestType(EzidRequestType.UPDATE);
        ezidQueue.save(bcid);
    }

    private URI generateBcidIdentifier(int bcidId) {
        // Create the shoulder Bcid (String Bcid Bcid)
        String shoulder = encoder.encode(new BigInteger(String.valueOf(bcidId)));

        String bow;
        if (props.ezidRequest()) {
            bow = scheme + "/" + props.naan() + "/";
        } else {
            bow = scheme + "/99999/fk4"; // ark:/99999/fk4 is a special testing identifier that is deleted periodically
        }

        // Create the identifier
        return URI.create(bow + shoulder);
    }

    /**
     * mint any Ezids that are in the queue
     */
    @Scheduled(fixedDelay = 1000 * 60 * 10) // every 10 mins
    public void mintEzids() {
        // NOTE: On any type of EZID error, we DON'T want to fail the process.. This means we need
        // a separate mechanism on the server side to check creation of EZIDs.  This is easy enough to do
        // in the Database.

        Set<String> successful = new HashSet<>();
        HashMap<String, String> ezidErrors = new HashMap<>();
        EzidUtils ezidUtils = new EzidUtils(props);

        List<Bcid> bcids = ezidQueue.getEzidRequests();
        if (bcids.size() > 0) {
            try {
                ezidService.login(props.ezidUser(), props.ezidPass());

                for (Bcid bcid : bcids) {
                    // Dublin Core metadata profile element
                    HashMap<String, String> map = ezidUtils.getDcMap(bcid);

                    // Register this as an EZID
                    try {

                        if (bcid.requestType() == EzidRequestType.MINT) {
                            ezidService.createIdentifier(String.valueOf(bcid.identifier()), map);
                        } else if (bcid.requestType() == EzidRequestType.UPDATE) {
                            ezidService.setMetadata(String.valueOf(bcid.identifier()), map);
                        } else {
                            ezidErrors.put(bcid.identifier().toString(), "unknown EzidRequestType " + bcid.requestType());
                            continue;
                        }

                        successful.add(bcid.identifier().toString());
                        logger.debug("minting{}", bcid.identifier().toString());
                    } catch (EzidException e) {
                        logger.error("Exception thrown in attempting to create EZID {}, a permission issue or identifier already exists?", bcid.identifier(), e);
                        ezidErrors.put(String.valueOf(bcid.identifier()), ExceptionUtils.getStackTrace(e));
                    }
                }

                if (successful.size() > 0) {
                    ezidQueue.delete(successful);
                }

            } catch (EzidException e) {
                ezidErrors.put("", ExceptionUtils.getStackTrace(e));
            }

            if (!ezidErrors.isEmpty()) {
                ezidUtils.sendErrorEmail(ezidErrors);
            }
        }

    }
}
