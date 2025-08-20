package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;
import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("tags")
public class TagController {
    private final TagService tagService;

    @PostMapping()
    public ResponseEntity<ClientResponseFormatDto> createTag(
            @Valid @RequestBody TagRequestDto tagRequestDto
    ) {
        TagResponseDto savedTag = this.tagService.create(tagRequestDto);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Tag created successfully!")
                .errors(null)
                .data(savedTag)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<ClientResponseFormatDto> fetchAllTags(Pageable pageable) {
        CustomPageResponse<TagResponseDto> tags = this.tagService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Fetch all tags")
                .errors(null)
                .data(tags)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
