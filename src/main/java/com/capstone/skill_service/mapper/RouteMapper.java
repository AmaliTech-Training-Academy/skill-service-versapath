package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.capsule.CapsuleSummaryResponseDto;
import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.TrackRequestDto;
import com.capstone.skill_service.dto.track.TrackResponseDto;
import com.capstone.skill_service.dto.track.TrackSummaryResponseDto;
import com.capstone.skill_service.dto.track.TrackWithCapsuleResponseDto;
import com.capstone.skill_service.model.GrowthTrackEntity;
import com.capstone.skill_service.model.RouteTrackMappingEntity;
import com.capstone.skill_service.model.TalentRouteEntity;
import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    @Mapping(target = "tracks", source = "tracks")
    RouteResponseDto toDto(TalentRouteEntity entity);

    TalentRouteEntity toEntity(RouteRequestDto dto);
    @Mapping(target = "tracks",  ignore = true)
    RouteWithTrackResponseDto toWithTrackDto(TalentRouteEntity entity);

    @Mapping(target = "id", source = "growthTrack.id")
    @Mapping(target = "name", source = "growthTrack.name")
    @Mapping(target = "status", source = "growthTrack.status")
    @Mapping(target = "estimatedMonths", source = "growthTrack.estimatedMonths")
    TrackSummaryResponseDto mapToTrackDto(RouteTrackMappingEntity mapping);

}
