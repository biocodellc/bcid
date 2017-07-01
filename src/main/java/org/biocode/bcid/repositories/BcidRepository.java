package org.biocode.bcid.repositories;

import org.biocode.bcid.models.Bcid;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


/**
 * This repositories provides CRUD operations for {@link Bcid} objects
 */
@Transactional
public interface BcidRepository extends Repository<Bcid, Integer>, QueryByExampleExecutor<Bcid> {

    @Modifying
    void delete(Bcid bcid);

    void save(Bcid bcid);

    Bcid findOneByIdentifier(@Param("identifier") String identifier);

    Set<Bcid> findAllByEzidRequestTrue();

    List<Bcid> findByExpeditionExpeditionIdAndResourceTypeNotIn(int expeditionId, String... datasetResourceType);

    Bcid findOneByTitleAndExpeditionExpeditionId(String title, int expeditionId);

    Set<Bcid> findAllByEzidRequestTrueAndEzidMadeFalse();

    @Query("select b from Bcid b where b.expedition.project.projectId=:projectId and b.expedition.expeditionCode=:expeditionCode " +
            "and b.resourceType=:resourceType and b.subResourceType=:subResourceType order by b.created desc ")
    List<Bcid> findAllByResourceTypeAndSubResourceType(@Param("projectId") int projectId,
                                                       @Param("expeditionCode") String expeditionCode,
                                                       @Param("resourceType") String resourceType,
                                                       @Param("subResourceType") String subResourceType);

    @Query("select b from Bcid b where b.expedition.project.projectId=:projectId and b.expedition.expeditionCode=:expeditionCode " +
            "and b.resourceType=:resourceType order by b.created desc ")
    List<Bcid> findAllByResourceType(@Param("projectId") int projectId,
                                     @Param("expeditionCode") String expeditionCode,
                                     @Param("resourceType") String resourceType);

    List<Bcid> findAllByEzidRequestFalse();
}
