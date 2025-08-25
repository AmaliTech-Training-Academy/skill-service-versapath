package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.GrowthTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<GrowthTrackEntity, UUID> {
    Optional<GrowthTrackEntity> findByName(String name);

    @Query("""
        SELECT gt
        FROM GrowthTrackEntity gt
        LEFT JOIN FETCH gt.skillCapsules c
        where gt.id = :id
    """)
    Optional<GrowthTrackEntity> findByIdWithCapsules(@Param("id") UUID id);

}
