package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.capsule.CapsuleSummaryResponseDto;
import com.capstone.skill_service.dto.track.TrackRequestDto;
import com.capstone.skill_service.dto.track.TrackResponseDto;
import com.capstone.skill_service.dto.track.TrackWithCapsuleResponseDto;
import com.capstone.skill_service.model.GrowthTrackEntity;
import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    @Mapping(target = "capsules", source = "skillCapsules")
    TrackResponseDto toDto(GrowthTrackEntity entity);

    GrowthTrackEntity toEntity(TrackRequestDto dto);
    @Mapping(target = "capsules",  ignore = true)
    TrackWithCapsuleResponseDto toWithCapsuleDto(GrowthTrackEntity entity);

    @Mapping(target = "id", source = "capsule.id")
    @Mapping(target = "name", source = "capsule.name")
    @Mapping(target = "difficulty", source = "capsule.difficulty")
    @Mapping(target = "proficiencyLevel", source = "capsule.proficiencyLevel")
    CapsuleSummaryResponseDto mapToCapsuleDto(TrackCapsuleMappingEntity mapping);

}
