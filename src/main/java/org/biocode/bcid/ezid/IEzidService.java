package org.biocode.bcid.ezid;

import java.util.HashMap;

/**
 * @author rjewing
 */
public interface IEzidService {
    void login(String username, String password) throws EzidException;

    void logout() throws EzidException;

    String createIdentifier(String identifier, HashMap<String, String> metadata) throws EzidException;

    String mintIdentifier(String shoulder, HashMap<String, String> metadata) throws EzidException;

    HashMap<String, String> getMetadata(String identifier) throws EzidException;

    void setMetadata(String identifier, HashMap<String, String> metadata) throws EzidException;

    void deleteIdentifier(String identifier) throws EzidException;
}
