package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("tags")
public class TagController {
    private final TagService tagService;

    @PostMapping()
    public ResponseEntity<TagResponseDto> createTag(
            @RequestBody TagRequestDto tagRequestDto
    ) {
        TagResponseDto savedTag = this.tagService.create(tagRequestDto);
        return ResponseEntity.ok(savedTag);
    }
}
