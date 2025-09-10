package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackCapsuleMappingRepository extends JpaRepository<TrackCapsuleMappingEntity, UUID> {
    @Query("""
    SELECT tc
    FROM TrackCapsuleMappingEntity tc
    JOIN FETCH tc.capsule
    WHERE tc.growthTrack.id IN :trackIds
    ORDER BY tc.sequenceOrder
""")
    List<TrackCapsuleMappingEntity> findByTrackIdsWithCapsules(@Param("trackIds") List<UUID> trackIds);

    @Query("""
    SELECT tc
    FROM TrackCapsuleMappingEntity tc
    JOIN FETCH tc.capsule
    WHERE tc.capsule.id = :capsuleId
    ORDER BY tc.sequenceOrder
""")
    List<TrackCapsuleMappingEntity> findBySkillCapsuleId(@Param("capsuleId") UUID capsuleId);

}
