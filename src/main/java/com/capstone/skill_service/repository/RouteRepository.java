package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.TalentRouteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<TalentRouteEntity, UUID> {
    Optional<TalentRouteEntity> findByName(String name);
    Optional<TalentRouteEntity> findByRoleName(String roleName);

    @Query("""
        SELECT tr
        FROM TalentRouteEntity tr
        LEFT JOIN FETCH tr.tracks mt
        LEFT JOIN FETCH mt.growthTrack gt
        WHERE tr.id = :id
    """)
    Optional<TalentRouteEntity> findByIdWithGrowthTracks(@Param("id") UUID id);

    @Query("""
        SELECT tr FROM TalentRouteEntity tr
        LEFT JOIN FETCH tr.tracks c
        ORDER BY tr.createdAt DESC
    """)
    Page<TalentRouteEntity> findAllWithGrowthTrack(Pageable pageable);

    @Query("""
        SELECT tr FROM TalentRouteEntity tr
        LEFT JOIN FETCH tr.tracks c
        WHERE (:name IS NULL OR LOWER(tr.name) LIKE LOWER(CONCAT('%', :name, '%')))
        ORDER BY tr.createdAt DESC
    """)
    Page<TalentRouteEntity> filterByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
}
