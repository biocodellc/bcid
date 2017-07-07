package org.biocode.bcid.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author rjewing
 */
public class Client {
    private String id;
    private String secret;
    private String accessToken;
    private Date tokenExpiration;

    @JsonCreator
    public Client(String id, String secret) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(secret, "secret must not be null");
        this.id = id;
        this.secret = secret;
    }

    public String id() {
        return id;
    }

    public String secret() {
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;

        Client client = (Client) o;

        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void setAccessToken(String accessToken, Date tokenExpiration) {
        this.accessToken = accessToken;
        this.tokenExpiration = tokenExpiration;
    }

    public Date tokenExpiration() {
        return tokenExpiration;
    }

    public String accessToken() {
        return accessToken;
    }

    public void revokeToken() {
        this.accessToken = null;
        this.tokenExpiration = null;
    }
}
