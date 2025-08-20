package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.exception.TagExistsException;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.repository.TagRepository;
import com.capstone.skill_service.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagResponseDto create(TagRequestDto dto) {
        if(findByName(dto.getName()).isPresent()){
            throw new TagExistsException(
                    String.format("A Tag with the email '%s' already exist",
                            dto.getName()));
        }
        TagEntity tagEntity = this.tagMapper.toEntity(dto);

        tagEntity.setCreatedAt(LocalDateTime.now());
        tagEntity.setUpdatedAt(LocalDateTime.now());

        return this.tagMapper.toDto(tagRepository.save(tagEntity));
    }

    @Override
    public Optional<TagEntity> findByName(String name) {
        return this.tagRepository.findByName(name);
    }

    @Override
    public Page<TagResponseDto> findAll(Pageable pageable) {
        Page<TagEntity> tags = this.tagRepository.findAll(pageable);
        return tags.map(this.tagMapper::toDto);
    }

    @Override
    public Optional<TagEntity> findById(UUID id) {
        return this.tagRepository.findById(id);
    }
}
