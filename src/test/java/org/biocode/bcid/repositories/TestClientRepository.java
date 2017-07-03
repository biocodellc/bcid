package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Client;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rjewing
 */
public class TestClientRepository implements ClientRepository {
    private Map<String, Client> storage = new HashMap<>();

    @Override
    public void save(Client client) {
        storage.put(client.id(), client);
    }

    @Override
    public Client getClientById(String id) {
        return storage.get(id);
    }

    @Override
    public Client getClientByAccessToken(String accessToken) {
        for (Client c: storage.values()) {
            if (c.accessToken().equals(accessToken)) {
                return c;
            }
        }
        return null;
    }
}
