package org.biocode.bcid.rest;


import org.biocode.bcid.models.Client;

/**
 * Bean for holding the Client for each REST request
 * @author RJ Ewing
 */
public class ClientContext {
    private Client client = null;

    public Client client() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
