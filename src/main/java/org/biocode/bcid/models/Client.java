package org.biocode.bcid.models;

import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

/**
 * @author rjewing
 */
@Entity
@Table(name = "clients")
public class Client {
    @Id
    private String id;
    private String secret;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "token_expiration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tokenExpiration;

    public Client(String id, String secret) {
        Assert.notNull(id);
        Assert.notNull(secret);
        this.id = id;
        this.secret = secret;
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
