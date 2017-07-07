package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Bcid;

import java.util.List;
import java.util.Set;

/**
 * @author rjewing
 */
public interface EzidQueue {

    List<Bcid> getEzidRequests();

    void delete(Set<String> identifiers);

    void save(Bcid bcid);

    int incrementId();
}
