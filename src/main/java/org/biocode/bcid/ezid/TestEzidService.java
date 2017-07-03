package org.biocode.bcid.ezid;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rjewing
 */
public class TestEzidService implements IEzidService {
    private Map<String, HashMap<String, String>> ezidRepo = new HashMap<>();

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
    }

    @Override
    public void deleteIdentifier(String identifier) throws EzidException {
        throw new NotImplementedException();
    }
}
