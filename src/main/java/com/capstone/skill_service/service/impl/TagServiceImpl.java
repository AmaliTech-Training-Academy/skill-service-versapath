package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.exception.TagExistsException;
import com.capstone.skill_service.exception.TagNotFoundException;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.repository.TagRepository;
import com.capstone.skill_service.service.TagService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);
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
        logger.info("Admin created skill tag: {}", tagEntity.getName());

        return this.tagMapper.toDto(tagRepository.save(tagEntity));
    }

    @Override
    public Optional<TagEntity> findByName(String name) {
        return this.tagRepository.findByName(name);
    }

    @Override
    public CustomPageResponse<TagResponseDto> findAll(Pageable pageable) {
        Page<TagEntity> tagList = this.tagRepository.findAll(pageable);
        Page<TagResponseDto> tags = tagList.map(this.tagMapper::toDto);

        logger.info("Tags list is fetched");

        return CustomPageResponse.<TagResponseDto>builder()
                .items(tags.getContent())
                .page(tags.getNumber())
                .size(tags.getSize())
                .totalElements(tags.getTotalElements())
                .totalPages(tags.getTotalPages())
                .hasNext(tags.hasNext())
                .hasPrevious(tags.hasPrevious())
                .build();
    }

    @Override
    public Optional<TagEntity> findById(UUID id) {
        return this.tagRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        TagEntity tag = findById(id)
                .orElseThrow( () -> new TagNotFoundException("A tag provided doesn't exist")
                );

        logger.info("Skill tag {} deleted", tag.getName());

        this.tagRepository.deleteById(id);
    }

    @Override
    public TagResponseDto partialUpdate(TagRequestDto dto, UUID id) {
        TagEntity tag = findById(id)
                .orElseThrow( () -> new TagNotFoundException("A tag provided doesn't exist")
                );
        if(tag.getName() != null){
            tag.setName(dto.getName());
        }
        if(tag.getType() != null){
            tag.setType(dto.getType());
        }
        if(tag.getDescription() != null){
            tag.setDescription(dto.getDescription());
        }
        if(tag.getUpdatedAt() != null){
            tag.setUpdatedAt(LocalDateTime.now());
        }

        logger.info("Skill tag {} updated", tag.getName());

        return this.tagMapper.toDto(this.tagRepository.save(tag));
    }
}
