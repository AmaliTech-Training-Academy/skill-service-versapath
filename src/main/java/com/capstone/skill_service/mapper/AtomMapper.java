package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.atom.AtomRequestDto;
import com.capstone.skill_service.dto.atom.AtomResponseDto;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillAtomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AtomMapper {
    AtomResponseDto toDto(SkillAtomEntity entity);
    SkillAtomEntity toEntity(AtomRequestDto dto);

    // map from junction entity to atomSummaryDto
    @Mapping(target = "id", source = "atom.id")
    @Mapping(target = "name", source = "atom.name")
    @Mapping(target = "description", source = "atom.description")
    @Mapping(target = "moodleSectionId", source = "atom.moodleSectionId")
    @Mapping(target = "sequenceOrder", source = "sequenceOrder")
    AtomInSequenceOrderResponseDto toInSequenceOrderDto(CapsuleAtomMappingEntity mapping);


}
