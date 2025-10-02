package com.capstone.skill_service.repository;

import com.capstone.skill_service.dto.track.TrackOnlyResponseDto;
import com.capstone.skill_service.model.GrowthTrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        SELECT DISTINCT gt
        FROM GrowthTrackEntity gt
        LEFT JOIN FETCH gt.skillCapsules cm
        LEFT JOIN FETCH cm.capsule c
        WHERE gt.id = :trackId
        ORDER BY cm.sequenceOrder
    """)
    Optional<GrowthTrackEntity> findByIdWithCapsules(@Param("trackId") UUID trackId);

    @Query("""
    SELECT new com.capstone.skill_service.dto.track.TrackOnlyResponseDto(
        t.id,t.name,t.description,t.estimatedMonths,t.status,t.image,(SELECT COUNT(mc.growthTrack.id) FROM t.skillCapsules mc),
        t.createdAt,t.updatedAt)
    FROM GrowthTrackEntity t
    """)
    Page<TrackOnlyResponseDto> findTrackWithCapsuleCount(Pageable pageable);

    @Query("""
    SELECT new com.capstone.skill_service.dto.track.TrackOnlyResponseDto(
        t.id,t.name,t.description,t.estimatedMonths,t.status,t.image,(SELECT COUNT(mc.growthTrack.id) FROM t.skillCapsules mc),
        t.createdAt,t.updatedAt)
    FROM GrowthTrackEntity t
    WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<TrackOnlyResponseDto> filterByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);


}
