package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Bcid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;


/**
 * This repositories provides CRUD operations for {@link Bcid} objects
 */
@Transactional
public interface BcidRepository extends Repository<Bcid, Integer> {

    @Modifying
    void delete(Bcid bcid);

    void save(Bcid bcid);

    Bcid findOneByIdentifier(@Param("identifier") URI identifier);

    Set<Bcid> findAllByEzidRequestTrue();

    Set<Bcid> findAllByEzidRequestTrueAndEzidMadeFalse();

    List<Bcid> findAllByEzidRequestFalse();
}
