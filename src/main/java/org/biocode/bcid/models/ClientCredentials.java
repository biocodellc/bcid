package org.biocode.bcid.models;

/**
 * @author rjewing
 */
public class ClientCredentials {
    public String id;
    public String secret;

    public ClientCredentials(String id, String secret) {
        this.id = id;
        this.secret = secret;
    }
}
