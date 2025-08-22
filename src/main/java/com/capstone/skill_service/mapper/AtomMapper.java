package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.atom.AtomRequestDto;
import com.capstone.skill_service.dto.atom.AtomResponseDto;
import com.capstone.skill_service.model.SkillAtomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AtomMapper {
    AtomResponseDto toDto(SkillAtomEntity entity);
    SkillAtomEntity toEntity(AtomRequestDto dto);

}
