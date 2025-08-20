package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("tags")
public class TagController {
    private final TagService tagService;

    @PostMapping()
    public ResponseEntity<TagResponseDto> createTag(
            @Valid @RequestBody TagRequestDto tagRequestDto
    ) {
        TagResponseDto savedTag = this.tagService.create(tagRequestDto);
        return ResponseEntity.ok(savedTag);
    }

    @GetMapping()
    public ResponseEntity<CustomPageResponse<TagResponseDto>> fetchAllTags(Pageable pageable) {
        CustomPageResponse<TagResponseDto> tags = this.tagService.findAll(pageable);
        return ResponseEntity.ok(tags);
    }
}
