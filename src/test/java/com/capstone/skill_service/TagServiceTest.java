package com.capstone.skill_service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.dto.tag.TagUpdateRequestDto;
import com.capstone.skill_service.exception.TagExistsException;
import com.capstone.skill_service.exception.TagNotFoundException;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.repository.TagRepository;
import com.capstone.skill_service.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private TagRequestDto requestDto;
    private TagEntity entity;
    private TagResponseDto responseDto;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        tagMapper = mock(TagMapper.class);

        tagService = new TagServiceImpl(tagRepository, tagMapper);

        requestDto = TagRequestDto.builder()
                .name("Java")
                .type("skill")
                .description("Java language tag")
                .build();
        entity = TagEntity.builder()
                .id(UUID.randomUUID())
                .name("Java")
                .type("skill")
                .description("Java language tag")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        responseDto = TagResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .description(entity.getDescription())
                .build();
    }

    @Test
    void create_shouldSaveTag_whenNotExists() {
        when(tagRepository.findByName("Java")).thenReturn(Optional.empty());
        when(tagMapper.toEntity(requestDto)).thenReturn(entity);
        when(tagRepository.save(entity)).thenReturn(entity);
        when(tagMapper.toDto(entity)).thenReturn(responseDto);

        TagResponseDto result = tagService.create(requestDto);

        assertNotNull(result);
        assertEquals("Java", result.getName());
        verify(tagRepository, times(1)).save(entity);
    }

    @Test
    void create_shouldThrowException_whenTagExists() {
        when(tagRepository.findByName("Java")).thenReturn(Optional.of(entity));

        assertThrows(TagExistsException.class, () -> tagService.create(requestDto));
        verify(tagRepository, never()).save(any());
    }

    @Test
    void findAll_shouldReturnPaginatedTags() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        Page<TagEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

        when(tagRepository.findAll(pageable)).thenReturn(page);
        when(tagMapper.toDto(entity)).thenReturn(responseDto);

        CustomPageResponse<TagResponseDto> result = tagService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Java", result.getItems().get(0).getName());
    }

    @Test
    void getTag_shouldReturnTag_whenExists() {
        UUID id = entity.getId();
        when(tagRepository.findById(id)).thenReturn(Optional.of(entity));
        when(tagMapper.toDto(entity)).thenReturn(responseDto);

        TagResponseDto result = tagService.getTag(id);

        assertNotNull(result);
        assertEquals("Java", result.getName());
    }

    @Test
    void getTag_shouldThrowException_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.getTag(id));
    }

    @Test
    void deleteById_shouldDelete_whenTagExists() {
        UUID id = entity.getId();
        when(tagRepository.findById(id)).thenReturn(Optional.of(entity));

        tagService.deleteById(id);

        verify(tagRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_shouldThrowException_whenNotExists() {
        UUID id = UUID.randomUUID();
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.deleteById(id));
        verify(tagRepository, never()).deleteById(id);
    }

    @Test
    void partialUpdate_shouldUpdateFields_whenTagExists() {
        UUID id = entity.getId();
        TagUpdateRequestDto updateDto = TagUpdateRequestDto.builder()
                .name("Spring")
                .type("framework")
                .description("Spring Boot tag")
                .build();
        when(tagRepository.findById(id)).thenReturn(Optional.of(entity));
        when(tagRepository.save(entity)).thenReturn(entity);
        when(tagMapper.toDto(entity)).thenReturn(responseDto);

        TagResponseDto result = tagService.partialUpdate(updateDto, id);

        assertNotNull(result);
        verify(tagRepository, times(1)).save(entity);
    }

    @Test
    void partialUpdate_shouldThrowException_whenNotExists() {
        UUID id = UUID.randomUUID();
        TagUpdateRequestDto updateDto = TagUpdateRequestDto.builder()
                .name("Spring")
                .type("framework")
                .description("Spring Boot tag")
                .build();

        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.partialUpdate(updateDto, id));
    }
}
