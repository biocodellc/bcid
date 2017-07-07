package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Client;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

/**
 * @author rjewing
 */
@Transactional
public interface ClientRepository {

    void save(Client client);

    Client getClientById(String id);

    Client getClientByAccessToken(String accessToken);

    void associateIdentifier(String clientId, URI identifier);

    boolean isAssociated(String id, URI identifier);
}
