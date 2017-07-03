package org.biocode.bcid.service;

import org.biocode.bcid.BcidEncoder;
import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.ezid.EzidException;
import org.biocode.bcid.ezid.TestEzidService;
import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.repositories.TestBcidRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.net.URI;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author rjewing
 */
public class BcidServiceTest {

    private BcidService bcidService;
    private TestBcidRepository repo;
    private TestEzidService ezidService;
    private MockEnvironment env;

    @Before
    public void setUp() {
        repo = new TestBcidRepository();
        ezidService = new TestEzidService();
        env = new MockEnvironment();
        env.setProperty("naan", "99999");
        env.setProperty("ezidRequests", "false");
        this.bcidService = new BcidService(repo, new BcidProperties(env), new BcidEncoder(), ezidService);
    }

    @Test
    public void should_generate_identifier_on_create() {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user")
                .build();

        bcidService.create(bcid);

        Bcid stored = repo.getBcid(bcid.id());

        assertNotNull(stored);
        assertEquals(URI.create("ark:/99999/fk4"), stored.identifier());
    }

    @Test
    public void should_override_bcid_ezidRequest_if_ezidRequests_prop_is_false() {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user")
                .ezidRequest(true)
                .build();

        bcidService.create(bcid);

        Bcid stored = repo.getBcid(bcid.id());

        assertNotNull(stored);
        assertEquals(false, stored.ezidRequest());
    }

    @Test
    public void should_replace_webAddress_ark_string_substitution() {
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user")
                .webAddress(URI.create("http://example.com/%7Bark%7D"))
                .build();

        bcidService.create(bcid);

        Bcid stored = repo.getBcid(bcid.id());

        assertNotNull(stored);
        assertEquals(URI.create("http://example.com/ark:/99999/fk4"), stored.webAddress());
    }

    @Test
    public void should_not_generate_ezid_if_ezidRequest_is_false() throws EzidException {
        enableEzidRequests();
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user")
                .ezidRequest(false)
                .build();

        bcidService.create(bcid);
        bcidService.createBcidsEZIDs();

        assertNull(ezidService.getMetadata(bcid.identifier().toString()));
    }

    @Test
    public void should_generate_ezid_if_ezidRequest_is_true() throws EzidException {
        enableEzidRequests();
        Bcid bcid = new Bcid.BcidBuilder("new resource", "demo user")
                .ezidRequest(true)
                .build();

        bcidService.create(bcid);
        bcidService.createBcidsEZIDs();

        HashMap<String, String> expectedMetadata = new HashMap<>();
        expectedMetadata.put("dc.publisher", "Biocode-BCID");
        expectedMetadata.put("_profile", "dc");
        expectedMetadata.put("dc.type", "new resource");
        expectedMetadata.put("dc.creator", "demo user");
        expectedMetadata.put("dc.title", "new resource");
        expectedMetadata.put("dc.date", "null"); // this is set via the db and we can't test that
        expectedMetadata.put("_target", "http://example.com/id/ark:/99999/fk4");

        assertEquals(expectedMetadata, ezidService.getMetadata(bcid.identifier().toString()));
    }

    @Test
    public void should_generate_ezid_for_all_bcids_where_ezidRequest_is_true_and_ezidMade_is_false() throws EzidException {
        enableEzidRequests();
        Bcid bcid1 = new Bcid.BcidBuilder("new resource", "demo user")
                .ezidRequest(true)
                .build();
        Bcid bcid2 = new Bcid.BcidBuilder("new resource", "demo user")
                .ezidRequest(true)
                .build();

        bcidService.create(bcid1);
        bcidService.create(bcid2);
        bcidService.createBcidsEZIDs();

        assertNotNull(ezidService.getMetadata(bcid1.identifier().toString()));
        assertNotNull(ezidService.getMetadata(bcid2.identifier().toString()));
    }

    private void enableEzidRequests() {
        env.setProperty("ezidRequests", "true");
        env.setProperty("ezidUser", "");
        env.setProperty("ezidPass", "");
        env.setProperty("publisher", "Biocode-BCID");
        env.setProperty("resolverTargetPrefix", "http://example.com/id/");
    }

}