package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.ClusterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClusterRepository extends JpaRepository<ClusterEntity, UUID> {
    Optional<ClusterEntity> findByName(String name);

    @Query("""
    SELECT cl
    FROM ClusterEntity cl
    JOIN cl.capsules c
    WHERE c.id IN :capsuleIds
""")
    List<ClusterEntity> findClustersByCapsuleIds(@Param("capsuleIds") List<UUID> capsuleIds);

}
