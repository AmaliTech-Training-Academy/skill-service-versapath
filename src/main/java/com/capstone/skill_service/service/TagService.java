package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.dto.tag.TagUpdateRequestDto;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.util.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TagService {
    TagResponseDto create(TagRequestDto dto);
    Optional<TagEntity> findByName(String name);
    CustomPageResponse<TagResponseDto> findAll(Pageable pageable);
    Optional<TagEntity> findById(UUID userId);
    TagResponseDto getTag(UUID userId);
    void deleteById(UUID id);
    TagResponseDto partialUpdate(TagUpdateRequestDto dto, UUID id);
    TagResponseDto updateStatus(Status status, UUID id);
}
