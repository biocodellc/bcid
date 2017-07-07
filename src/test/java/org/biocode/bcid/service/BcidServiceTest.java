package org.biocode.bcid.service;

import org.biocode.bcid.BcidEncoder;
import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.ezid.EzidException;
import org.biocode.bcid.ezid.EzidRequestType;
import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.repositories.TestClientRepository;
import org.biocode.bcid.repositories.TestEzidQueue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author rjewing
 */
public class BcidServiceTest {

    private BcidService bcidService;
    private TestEzidQueue queue;
    private TestEzidService ezidService;
    private MockEnvironment env;
    private TestClientRepository clientRepository;

    @Before
    public void setUp() {
        queue = new TestEzidQueue();
        clientRepository = new TestClientRepository();
        ezidService = new TestEzidService();
        env = new MockEnvironment();
        env.setProperty("naan", "88888");
        env.setProperty("ezidUser", "");
        env.setProperty("ezidPass", "");
        env.setProperty("ezidRequests", "false");
        this.bcidService = new BcidService(queue, clientRepository, new BcidProperties(env), new BcidEncoder(), ezidService);
    }

    @Test
    public void should_generate_test_identifier_on_create_no_ezid_request() throws EzidException {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user", "fims")
                .build();
        bcid.setCreated(new Date());

        bcidService.create(bcid, "client1");

        Bcid stored = queue.get(bcid.identifier());

        assertNotNull(stored);
        assertEquals(URI.create("ark:/99999/fk4B2"), stored.identifier());

        bcidService.mintEzids();

        assertNotNull(ezidService.getMetadata(bcid.identifier().toString()));
        assertTrue(clientRepository.isAssociated("client1", bcid.identifier()));
    }

    @Test
    public void should_generate_test_identifier_when_bcid_ezid_request_no_app_ezid_request() throws EzidException {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user", "fims")
                .ezidRequest(true)
                .build();

        bcidService.create(bcid, "client1");

        assertEquals(URI.create("ark:/99999/fk4B2"), bcid.identifier());
    }

    @Test
    public void should_replace_webAddress_ark_string_substitution() {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user", "fims")
                .webAddress(URI.create("http://example.com/%7Bark%7D"))
                .build();

        bcidService.create(bcid, "client1");

        Bcid stored = queue.get(bcid.identifier());

        assertNotNull(stored);
        assertEquals(URI.create("http://example.com/ark:/99999/fk4B2"), stored.webAddress());
    }

    @Test
    public void should_generate_non_test_identifier_if_ezidRequest_is_true() throws EzidException {
        env.setProperty("ezidRequests", "true");
        Date now = new Date();
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user", "fims")
                .webAddress(URI.create("http://example.com/id/%7Bark%7D"))
                .build();
        bcid.setCreated(now);

        bcidService.create(bcid, "client1");
        bcidService.mintEzids();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        HashMap<String, String> expectedMetadata = new HashMap<>();
        expectedMetadata.put("dc.publisher", "fims");
        expectedMetadata.put("_profile", "dc");
        expectedMetadata.put("dc.type", "new resource");
        expectedMetadata.put("dc.creator", "demo user");
        expectedMetadata.put("dc.title", "new resource");
        expectedMetadata.put("dc.date", formatter.format(now)); // this is set via the db and we can't test that
        expectedMetadata.put("_target", "http://example.com/id/ark:/88888/B2");

        assertEquals(expectedMetadata, ezidService.getMetadata(bcid.identifier().toString()));
        assertEquals(0, queue.size());
    }

    @Test
    public void should_call_correct_ezid_service_method_depending_on_request_type() throws EzidException {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user", "fims")
                .build();
        bcid.setCreated(new Date());

        Bcid bcid2 = new Bcid.BcidBuilder("new resource", "demo user", "fims")
                .build();
        bcid2.setIdentifier(URI.create("ark:/99999/fk4z2"));
        bcid2.setCreated(new Date());

        bcidService.create(bcid, "client1");
        bcidService.update(bcid2);

        bcidService.mintEzids();

        assertEquals(EzidRequestType.MINT, bcid.requestType());
        assertEquals(EzidRequestType.UPDATE, bcid2.requestType());

        assertFalse(ezidService.wasUpdated(bcid.identifier().toString()));
        assertTrue(ezidService.wasUpdated(bcid2.identifier().toString()));
    }
}