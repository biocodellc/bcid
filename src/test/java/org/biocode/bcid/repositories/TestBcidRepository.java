package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Bcid;

import java.net.URI;
import java.util.*;

/**
 * @author rjewing
 */
public class TestBcidRepository implements BcidRepository {
    private LinkedList<Bcid> storage;

    public TestBcidRepository() {
        this.storage = new LinkedList<>();
    }

    @Override
    public void delete(Bcid bcid) {
        storage.remove(bcid);
    }

    @Override
    public void save(Bcid bcid) {
        if (bcid.id() == 0) {
            bcid.setId(storage.size() + 1);
            storage.add(bcid);
            return;
        }

        for (Bcid b: storage) {
            if (b.id() == bcid.id()) {
                storage.remove(b);
                storage.add(bcid);
                return;
            }
        }
    }

    @Override
    public Bcid findOneByIdentifier(URI identifier) {

        for (Bcid b: storage) {
            if (identifier.equals(identifier)) {
                return b;
            }
        }

        return null;
    }

    public Bcid getBcid(int id) {
        for (Bcid b: storage) {
            if (b.id() == id) {
                return b;
            }
        }

        return null;
    }

    @Override
    public Set<Bcid> findAllByEzidRequestTrue() {
        return null;
    }

    @Override
    public Set<Bcid> findAllByEzidRequestTrueAndEzidMadeFalse() {
        return null;
    }

    @Override
    public List<Bcid> findAllByEzidRequestFalse() {
        return null;
    }
}
