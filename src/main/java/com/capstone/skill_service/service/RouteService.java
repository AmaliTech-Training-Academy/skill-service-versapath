package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteUpdateRequestDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.model.TalentRouteEntity;
import com.capstone.skill_service.util.Status;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RouteService {
    RouteResponseDto create(RouteRequestDto dto);
    Optional<TalentRouteEntity> findByName(String name);
    Optional<TalentRouteEntity> findByRoleName(String roleName);
    Optional<TalentRouteEntity> findById(UUID id);
    CustomPageResponse<RouteResponseDto> findAll(Pageable pageable);
    RouteResponseDto getRoute(UUID id);
    RouteWithTrackResponseDto getRouteWithTracks(UUID routeId);
    void deleteById(UUID id);
    RouteResponseDto partialUpdate(RouteUpdateRequestDto dto, UUID id);
    RouteResponseDto updateStatus(Status status, UUID id);
    RouteResponseDto assignTrackToRoute(UUID routeId, TrackIdsRequestDto dto);
    RouteResponseDto removeTrackFromRoute(UUID routeId, UUID trackId);
    RouteResponseDto reorderTracks(UUID routeId, List<UUID> orderedTrackIds);

}
