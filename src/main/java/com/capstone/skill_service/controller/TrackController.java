package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;
import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.capsule.CapsuleIdsRequestDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.service.TrackService;
import com.capstone.skill_service.util.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("growth-tracks")
@Tag(name = "Growth Tracks Controller", description = "Manage all growth track's api")
public class TrackController {
    private final TrackService trackService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create growth track", description = "This end point allows only admin to create a growth track")
    public ResponseEntity<ClientResponseFormatDto> createTrack(
            @Valid @RequestBody TrackRequestDto trackRequestDto
    ) {
        TrackResponseDto savedTrack = this.trackService.create(trackRequestDto);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Track created successfully!")
                .errors(null)
                .data(Map.of("item", savedTrack))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @Operation(summary = "Retrieve growth tracks", description = "This end point allows anyone to fetch all growth tracks")
    public ResponseEntity<ClientResponseFormatDto> fetchAllTracks(Pageable pageable) {
        CustomPageResponse<TrackOnlyResponseDto> tracks = this.trackService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Fetch all growth tracks")
                .errors(null)
                .data(tracks)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{trackId}")
    @Operation(summary = "Retrieve growth track", description = "This end point allows anyone to fetch all growth tracks")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleTrack(@PathVariable UUID trackId) {
        TrackWithCapsuleResponseDto track = this.trackService.getTrackWithCapsules(trackId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Track retrieved successfully")
                .errors(null)
                .data(Map.of("item", track))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(name = "delete_track", path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete Track",
            description = "The track is deleted using its id that is passed as parameter")
    public ResponseEntity<ClientResponseFormatDto> deleteTrack(@PathVariable UUID id){
        this.trackService.deleteById(id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Growth Track deleted successfully!")
                .errors(null)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{trackId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update growth track", description = "This end point allows admin to update a growth track")
    public ResponseEntity<ClientResponseFormatDto> updateTrack(
            @Valid @RequestBody TrackUpdateRequestDto trackRequestDto, @PathVariable UUID trackId) {
        TrackResponseDto updatedTrack = this.trackService.partialUpdate(trackRequestDto, trackId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Growth Track updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedTrack))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update growth track status", description = "This end point allows admin to update a growth track as a soft delete")
    public ResponseEntity<ClientResponseFormatDto> updateTrackStatus(
            @RequestParam UUID id, @RequestParam Status status) {
        TrackResponseDto updatedTrack = this.trackService.updateStatus(status, id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Growth Track status updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedTrack))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/assignCapsule/{trackId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Assign new skill capsule to a growth track", description = "This end point allows admin to add new skill capsule to a growth track")
    public ResponseEntity<ClientResponseFormatDto> assignCapsuleToTrack(
            @RequestBody CapsuleIdsRequestDto atomIds, @PathVariable UUID trackId) {
        TrackResponseDto updatedTrack = this.trackService.assignCapsuleToTrack(trackId, atomIds);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsules added to a growth track successfully!")
                .errors(null)
                .data(Map.of("item", updatedTrack))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/removeCapsule")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Remove a skill capsule from track", description = "This end point allows admin to remove a skill capsule from track")
    public ResponseEntity<ClientResponseFormatDto> removeCapsuleFromTrack(
            @RequestParam UUID trackId, @RequestParam UUID capsuleId) {
        this.trackService.removeCapsuleFromTrack(trackId, capsuleId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsule removed from track successfully!")
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/reorderCapsule/{trackId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Reorder new skill capsule in a growth track collection", description = "This end point allows admin " +
            "to shift capsule sequence order in a growth track however they want")
    public ResponseEntity<ClientResponseFormatDto> reorderCapsule(
            @RequestBody CapsuleIdsRequestDto atomIds, @PathVariable UUID trackId) {
        TrackResponseDto updatedTrack = this.trackService.reorderCapsules(trackId, atomIds.getCapsuleIds());
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsules reordered in track successfully!")
                .errors(null)
                .data(Map.of("item", updatedTrack))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
