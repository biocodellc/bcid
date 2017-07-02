package org.biocode.bcid.service;

import org.biocode.bcid.PasswordHash;
import org.biocode.bcid.StringGenerator;
import org.biocode.bcid.models.Client;
import org.biocode.bcid.models.ClientCredentials;
import org.biocode.bcid.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class ClientService {
    public static final int TOKEN_EXPIRATION = 60 * 60 * 24;

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientCredentials create() {
        String id = StringGenerator.generateString(20);
        String secret = StringGenerator.generateString(75);

        String secretHash = PasswordHash.createHash(secret);

        Client client = new Client(id, secretHash);

        clientRepository.save(client);

        return new ClientCredentials(id, secret);
    }

    @Transactional
    public Client update(Client client) {
        clientRepository.save(client);
        return client;
    }

    @Transactional(readOnly = true)
    public Client getClient(String id, String secret) {
        Client client = clientRepository.getClientById(id);

        if (client != null && PasswordHash.validatePassword(secret, client.secret())) {
            return client;
        }

        return null;
    }

    @Transactional(readOnly = true)
    public Client getClient(String accessToken) {
        Client client = clientRepository.getClientByAccessToken(accessToken);

        if (client == null || new Date().after(client.tokenExpiration())) {
            return null;
        }

        return client;
    }

    public String generateToken(Client client) {
        if (client.accessToken() != null && new Date().before(client.tokenExpiration())) {
            return client.accessToken();
        }
        String token = StringGenerator.generateString(20);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, TOKEN_EXPIRATION);

        client.setAccessToken(token, calendar.getTime());
        clientRepository.save(client);

        return token;
    }
}
