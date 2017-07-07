package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Bcid;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rjewing
 */
public class TestEzidQueue implements EzidQueue {
    private List<Bcid> storage;
    private int id = 2; // start here to avoid the fk4 bcid identifer

    public TestEzidQueue() {
        this.storage = new ArrayList<>();
    }

    @Override
    public List<Bcid> getEzidRequests() {
        return storage;
    }

    @Override
    public void delete(List<String> identifiers) {
        storage = storage.stream()
                .filter((b) -> !identifiers.contains(b.identifier().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Bcid bcid) {
        for (Bcid b : storage) {
            if (b.identifier() == bcid.identifier()) {
                storage.remove(b);
                storage.add(bcid);
                return;
            }
        }

        storage.add(bcid);
    }

    @Override
    public int incrementId() {
        return id++;
    }

    public Bcid get(URI identifier) {
        for (Bcid b: storage) {
            if (b.identifier().equals(identifier)) {
                return b;
            }
        }

        return null;
    }

    public int size() {
        return storage.size();
    }
}
