package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;
import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.dto.tag.TagUpdateRequestDto;
import com.capstone.skill_service.service.TagService;
import com.capstone.skill_service.util.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("tags")
@Tag(name = "Tags Controller", description = "Manage all skill tag's api")
public class TagController {
    private final TagService tagService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create skill tag", description = "This end point allows only admin to create a skill tag")
    public ResponseEntity<ClientResponseFormatDto> createTag(
            @Valid @RequestBody TagRequestDto tagRequestDto
    ) {
        TagResponseDto savedTag = this.tagService.create(tagRequestDto);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tag created successfully!")
                .errors(null)
                .data(Map.of("item", savedTag))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @Operation(summary = "Retrieve skill tags", description = "This end point allows any one to fetch all skill tags")
    public ResponseEntity<ClientResponseFormatDto> fetchAllTags(Pageable pageable) {
        CustomPageResponse<TagResponseDto> tags = this.tagService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Fetch all tags")
                .errors(null)
                .data(tags)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter skill tags", description = "This end point allows any one to fetch all skill tags")
    public ResponseEntity<ClientResponseFormatDto> filterTags(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "${PAGE_SIZE}") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        CustomPageResponse<TagResponseDto> tags = this.tagService.filterTags(name, pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Filter tags")
                .errors(null)
                .data(tags)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{tagId}")
    @Operation(summary = "Retrieve skill tags", description = "This end point allows anyone to fetch all skill tags")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleTag(@PathVariable UUID tagId) {
        TagResponseDto tag = this.tagService.getTag(tagId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tag retrieved successfully")
                .errors(null)
                .data(Map.of("item", tag))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(name = "delete_tag", path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete Tag",
            description = "The tag is deleted using its id that is passed as parameter")
    public ResponseEntity<ClientResponseFormatDto> deleteTag(@PathVariable UUID id){
        this.tagService.deleteById(id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tag deleted successfully!")
                .errors(null)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{tagId}")
    @Operation(summary = "Update skill tag", description = "This end point allows admin to update a skill tag")
    public ResponseEntity<ClientResponseFormatDto> updateTag(
            @Valid @RequestBody TagUpdateRequestDto tagRequestDto, @PathVariable UUID tagId) {

        TagResponseDto updatedTag = this.tagService.partialUpdate(tagRequestDto, tagId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tag updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedTag))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update skill tag status", description = "This end point allows admin to update a skill tag as a soft delete")
    public ResponseEntity<ClientResponseFormatDto> updateClusterStatus(
            @RequestParam UUID id, @RequestParam Status status) {
        TagResponseDto updatedTag = this.tagService.updateStatus(status, id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tag status updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedTag))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/addMultipleTags")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create multiple skill tags", description = "This end point allows only admin to create multiple skill tags")
    public ResponseEntity<ClientResponseFormatDto> createMultipleTags(
            @RequestBody List<String> tagNames
    ) {
        List<TagResponseDto> savedTags = this.tagService.addMultipleTags(tagNames);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tags created successfully!")
                .errors(null)
                .data(Map.of("item", savedTags))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
