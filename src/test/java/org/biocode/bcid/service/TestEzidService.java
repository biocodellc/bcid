package org.biocode.bcid.service;

import org.apache.commons.lang.NotImplementedException;
import org.biocode.bcid.ezid.EzidException;
import org.biocode.bcid.ezid.IEzidService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rjewing
 */
public class TestEzidService implements IEzidService {
    private Map<String, HashMap<String, String>> ezidRepo = new HashMap<>();
    List<String> updatedIdentifiers = new ArrayList<>();

    @Override
    public void login(String username, String password) throws EzidException {
    }

    @Override
    public void logout() throws EzidException {
    }

    @Override
    public String createIdentifier(String identifier, HashMap<String, String> metadata) throws EzidException {
        ezidRepo.put(identifier, metadata);
        return identifier;
    }

    @Override
    public String mintIdentifier(String shoulder, HashMap<String, String> metadata) throws EzidException {
        throw new NotImplementedException();
    }

    @Override
    public HashMap<String, String> getMetadata(String identifier) throws EzidException {
        return ezidRepo.get(identifier);
    }

    @Override
    public void setMetadata(String identifier, HashMap<String, String> metadata) throws EzidException {
        ezidRepo.put(identifier, metadata);
        updatedIdentifiers.add(identifier);
    }

    @Override
    public void deleteIdentifier(String identifier) throws EzidException {
        throw new NotImplementedException();
    }

    public boolean wasUpdated(String identifier) {
        return updatedIdentifiers.contains(identifier);
    }
}
