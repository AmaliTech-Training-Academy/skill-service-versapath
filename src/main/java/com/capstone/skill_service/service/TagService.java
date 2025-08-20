package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;

public interface TagService {
    TagResponseDto create(TagRequestDto dto);
}
