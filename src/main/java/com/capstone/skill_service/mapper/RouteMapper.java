package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.TrackSummaryResponseDto;
import com.capstone.skill_service.model.RouteTrackMappingEntity;
import com.capstone.skill_service.model.TalentRouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TrackMapper.class)
public interface RouteMapper {
    @Mapping(target = "tracks", source = "tracks")
    RouteResponseDto toDto(TalentRouteEntity entity);

    TalentRouteEntity toEntity(RouteRequestDto dto);
    @Mapping(target = "tracks",  source = "tracks")
    RouteWithTrackResponseDto toWithTrackDto(TalentRouteEntity entity);

    @Mapping(target = "id", source = "growthTrack.id")
    @Mapping(target = "name", source = "growthTrack.name")
    @Mapping(target = "status", source = "growthTrack.status")
    @Mapping(target = "estimatedMonths", source = "growthTrack.estimatedMonths")
    TrackSummaryResponseDto mapToTrackDto(RouteTrackMappingEntity mapping);

}
