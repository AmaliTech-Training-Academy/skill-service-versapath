package com.capstone.skill_service.repository;

import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.model.ClusterEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

@Repository
public interface ClusterRepository extends JpaRepository<ClusterEntity, UUID> {
    Optional<ClusterEntity> findByName(String name);

    @Query("""
        select cl
        from ClusterEntity cl
        left join fetch cl.capsules c
        where cl.id = :id
    """)
    Optional<ClusterEntity> findByIdWithCapsules(@Param("id") UUID id);

    @Query("""
    SELECT new com.capstone.skill_service.dto.cluster.ClusterResponseDto(
            cl.id, cl.name,cl.type,cl.description,cl.imageName,cl.status,
            (SELECT COUNT(c.id) FROM cl.capsules c), cl.createdAt, cl.updatedAt
        )
        FROM ClusterEntity cl
""")
    Page<ClusterResponseDto> findClustersWithCapsuleCount(Pageable pageable);

    @Query("""
    SELECT c.id
    FROM ClusterEntity cl
    JOIN cl.capsules c
    WHERE cl.id = :clusterId
""")
    List<UUID> findCapsuleIdsByClusterId(@Param("clusterId") UUID clusterId);


    @Query("""
    SELECT new com.capstone.skill_service.dto.cluster.ClusterResponseDto(
            cl.id, cl.name,cl.type,cl.description,cl.imageName,cl.status,
            (SELECT COUNT(c.id) FROM cl.capsules c), cl.createdAt, cl.updatedAt
        )
        FROM ClusterEntity cl
        WHERE (:name IS NULL OR LOWER(cl.name) LIKE LOWER(CONCAT('%', :name, '%')))
""")
    Page<ClusterResponseDto> findByNameContainingIgnoreCase(@Param("name") String name,Pageable pageable);

}
