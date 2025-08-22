package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.capsule.CapsuleRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleResponseDto;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapsuleMapper {
    @Mapping(target = "skillAtoms", source = "skillAtoms")
    CapsuleResponseDto toDto(SkillCapsuleEntity entity);

    @Mapping(target = "skillAtoms", ignore = true) // handle mapping in service
    SkillCapsuleEntity toEntity(CapsuleRequestDto dto);

}
