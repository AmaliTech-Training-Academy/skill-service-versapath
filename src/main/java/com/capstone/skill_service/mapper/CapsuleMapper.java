package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleSummaryWithNoClusterResponseDto;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapsuleMapper {
    @Mapping(target = "skillAtoms", source = "skillAtoms")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "clusters", source = "clusters")
    CapsuleResponseDto toDto(SkillCapsuleEntity entity);

    // map from junction entity to atomSummaryDto
    @Mapping(target = "id", source = "atom.id")
    @Mapping(target = "name", source = "atom.name")
    @Mapping(target = "description", source = "atom.description")
    @Mapping(target = "moodleSectionId", source = "atom.moodleSectionId")
    @Mapping(target = "sequenceOrder", source = "sequenceOrder")
    AtomInSequenceOrderResponseDto toAtomDto(CapsuleAtomMappingEntity mapping);

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
    @Mapping(target = "skillAtoms", source = "capsule.skillAtoms")
    @Mapping(target = "sequenceOrder", source = "sequenceOrder")
    CapsuleInSequenceOrderResponseDto toInSequenceOrderDto(TrackCapsuleMappingEntity mapping);

}
