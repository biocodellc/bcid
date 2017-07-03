package org.biocode.bcid.service;

import org.biocode.bcid.models.Client;
import org.biocode.bcid.models.ClientCredentials;
import org.biocode.bcid.repositories.TestClientRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author rjewing
 */
public class ClientServiceTest {

    private TestClientRepository clientRepository;
    private ClientService clientService;

    @Before
    public void setUp() {
        this.clientRepository = new TestClientRepository();
        this.clientService = new ClientService(this.clientRepository);
    }

    @Test
    public void should_get_client_by_id_and_secret() {
        ClientCredentials creds = clientService.create();

        Client client = clientService.getClient(creds.id, creds.secret);

        assertNotNull(client);
    }

    @Test
    public void should_get_client_by_valid_accessToken() {
        Client client = getNewClient();

        String accessToken = clientService.generateToken(client);
        Client authenticated = clientService.getClient(accessToken);

        assertNotNull(authenticated);
    }

    @Test
    public void should_not_get_client_with_expired_accessToken() throws InterruptedException {
        Client client = getNewClient();

        String accessToken = clientService.generateToken(client);

        client.setAccessToken(accessToken, new Date());
        TimeUnit.SECONDS.sleep(1);
        Client authenticated = clientService.getClient(accessToken);

        assertNull(authenticated);
    }

    @Test
    public void should_return_existing_accessToken_if_still_valid() throws InterruptedException {
        Client client = getNewClient();

        String accessToken = clientService.generateToken(client);
        String accessToken2 = clientService.generateToken(client);

        assertEquals(accessToken, accessToken2);
    }

    private Client getNewClient() {
        ClientCredentials creds = clientService.create();

        Client client = clientService.getClient(creds.id, creds.secret);

        assertNotNull(client);
        return client;
    }

}