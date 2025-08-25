package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.track.TrackRequestDto;
import com.capstone.skill_service.dto.track.TrackResponseDto;
import com.capstone.skill_service.dto.track.TrackWithCapsuleResponseDto;
import com.capstone.skill_service.model.GrowthTrackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    TrackResponseDto toDto(GrowthTrackEntity entity);

    GrowthTrackEntity toEntity(TrackRequestDto dto);
    @Mapping(target = "capsules",  ignore = true)
    TrackWithCapsuleResponseDto toWithCapsuleDto(GrowthTrackEntity entity);

}
