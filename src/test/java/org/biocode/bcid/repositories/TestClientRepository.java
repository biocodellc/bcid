package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Client;

import java.net.URI;
import java.util.*;

/**
 * @author rjewing
 */
public class TestClientRepository implements ClientRepository {
    private Map<String, Client> storage = new HashMap<>();
    private Map<String, List<URI>> identifierStorage = new HashMap<>();

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
        for (Client c : storage.values()) {
            if (c.accessToken().equals(accessToken)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void associateIdentifier(String clientId, URI identifier) {
        identifierStorage.computeIfAbsent(clientId, k -> new ArrayList<>())
                .add(identifier);
    }


    @Override
    public boolean isAssociated(String clientId, URI identifier) {
        return identifierStorage.getOrDefault(clientId, new ArrayList<>()).contains(identifier);
    }
}

