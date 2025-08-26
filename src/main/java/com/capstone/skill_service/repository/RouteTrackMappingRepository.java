package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.RouteTrackMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteTrackMappingRepository extends JpaRepository<RouteTrackMappingEntity, UUID> {
    @Query("""
    SELECT rt
    FROM RouteTrackMappingEntity rt
    JOIN FETCH rt.growthTrack
    WHERE rt.talentRoute.id = :trackId
    ORDER BY rt.talentRoute.id, rt.sequenceOrder
""")
    List<RouteTrackMappingEntity> findByTrackIdsWithCapsules(@Param("trackId") UUID trackId);

}
