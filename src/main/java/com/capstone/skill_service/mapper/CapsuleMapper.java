package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.capsule.*;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AtomMapper.class)
public interface CapsuleMapper {
    CapsuleResponseDto toDto(SkillCapsuleEntity entity);

    @Mapping(target = "skillAtoms", source = "skillAtoms")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "clusters", source = "clusters")
    CapsuleWithDetailsResponseDto toWithDetailsDto(SkillCapsuleEntity entity);

    CapsuleOnlyResponseDto toCapsuleOnlyDto(SkillCapsuleEntity entity);

    @Mapping(target = "skillAtoms", ignore = true) // handle mapping in service
    SkillCapsuleEntity toEntity(CapsuleRequestDto dto);

    @Mapping(target = "skillAtoms", source = "skillAtoms")
    @Mapping(target = "tags", source = "tags")
    CapsuleSummaryWithNoClusterResponseDto toNoClusterDto(SkillCapsuleEntity entity);

    @Mapping(target = "id", source = "capsule.id")
    @Mapping(target = "name", source = "capsule.name")
    @Mapping(target = "estimatedHours", source = "capsule.estimatedHours")
    @Mapping(target = "difficulty", source = "capsule.difficulty")
    @Mapping(target = "proficiencyLevel", source = "capsule.proficiencyLevel")
    @Mapping(target = "description", source = "capsule.description")
    @Mapping(target = "skillAtoms", ignore = true) // to avoid N+1 caused by capsule.skillAtoms. service handles it
    @Mapping(target = "sequenceOrder", source = "sequenceOrder")
    CapsuleInSequenceOrderResponseDto toInSequenceOrderDto(TrackCapsuleMappingEntity mapping);

}
