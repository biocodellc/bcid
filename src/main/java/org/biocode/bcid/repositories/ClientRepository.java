package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Client;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rjewing
 */
@Transactional
public interface ClientRepository extends Repository<Client, String> {

    void save(Client client);

    Client getClientById(String id);

    Client getClientByAccessToken(String accessToken);
}
