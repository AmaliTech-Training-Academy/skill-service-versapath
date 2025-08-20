package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.model.TagEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponseDto toDto(TagEntity entity);
    TagEntity toEntity(TagRequestDto dto);

}
