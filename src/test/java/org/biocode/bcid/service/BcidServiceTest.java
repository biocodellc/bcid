package org.biocode.bcid.service;

import org.biocode.bcid.BcidEncoder;
import org.biocode.bcid.BcidProperties;
import org.biocode.bcid.models.Bcid;
import org.biocode.bcid.repositories.TestBcidRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.net.URI;

import static org.junit.Assert.*;

/**
 * @author rjewing
 */
public class BcidServiceTest {

    private BcidService bcidService;
    private TestBcidRepository repo;

    @Before
    public void setUp() {
        this.repo = new TestBcidRepository();
        MockEnvironment env = new MockEnvironment();
        env.setProperty("naan", "99999");
        env.setProperty("ezidRequests", "false");
        this.bcidService = new BcidService(repo, new BcidProperties(env), new BcidEncoder());
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

}