package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.repository.TagRepository;
import com.capstone.skill_service.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagResponseDto create(TagRequestDto dto) {
        TagEntity tagEntity = this.tagMapper.toEntity(dto);

        tagEntity.setCreatedAt(LocalDateTime.now());
        tagEntity.setUpdatedAt(LocalDateTime.now());

        return this.tagMapper.toDto(tagRepository.save(tagEntity));
    }
}
