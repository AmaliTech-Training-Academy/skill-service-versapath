package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;
import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomIdsRequestDto;
import com.capstone.skill_service.dto.capsule.*;
import com.capstone.skill_service.dto.cluster.ClusterIdsRequestDto;
import com.capstone.skill_service.dto.tag.TagIdsRequestDto;
import com.capstone.skill_service.service.CapsuleService;
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
@RequestMapping("capsules")
@Tag(name = "Capsules Controller", description = "Manage all skill capsule's api")
public class CapsuleController {
    private final CapsuleService capsuleService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create skill capsule", description = "This end point allows only admin to create a skill capsule")
    public ResponseEntity<ClientResponseFormatDto> createCapsule(
            @Valid @RequestBody CapsuleRequestDto capsuleRequestDto
    ) {
        CapsuleResponseDto savedCapsule = this.capsuleService.create(capsuleRequestDto);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsule created successfully!")
                .errors(null)
                .data(Map.of("item", savedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @Operation(summary = "Retrieve skill capsules", description = "This end point allows anyone to fetch all skill capsules")
    public ResponseEntity<ClientResponseFormatDto> fetchAllCapsules(Pageable pageable) {
        CustomPageResponse<CapsuleOnlyResponseDto> capsules = this.capsuleService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Fetch all capsules")
                .errors(null)
                .data(capsules)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{capsuleId}")
    @Operation(summary = "Retrieve skill capsules", description = "This end point allows anyone to fetch all skill capsules")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleCapsule(@PathVariable UUID capsuleId) {
        CapsuleWithDetailsResponseDto capsule = this.capsuleService.getCapsuleWithDetails(capsuleId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsule retrieved successfully")
                .errors(null)
                .data(Map.of("item", capsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(name = "delete_capsule", path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete Capsule",
            description = "The capsule is deleted using its id that is passed as parameter")
    public ResponseEntity<ClientResponseFormatDto> deleteCapsule(@PathVariable UUID id){
        this.capsuleService.deleteById(id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsule deleted successfully!")
                .errors(null)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{capsuleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update skill capsule", description = "This end point allows admin to update a skill capsule")
    public ResponseEntity<ClientResponseFormatDto> updateCapsule(
            @Valid @RequestBody CapsuleUpdateRequestDto capsuleRequestDto, @PathVariable UUID capsuleId) {
        CapsuleResponseDto updatedCapsule = this.capsuleService.partialUpdate(capsuleRequestDto, capsuleId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsule updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update skill capsule status", description = "This end point allows admin to update a skill capsule as a soft delete")
    public ResponseEntity<ClientResponseFormatDto> updateCapsuleStatus(
            @RequestParam UUID id, @RequestParam Status status) {
        CapsuleResponseDto updatedCapsule = this.capsuleService.updateStatus(status, id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Capsule status updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/assignAtom/{capsuleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Assign new skill atom to capsule", description = "This end point allows admin to add new skill atom to capsule")
    public ResponseEntity<ClientResponseFormatDto> assignAtomToCapsule(
            @RequestBody AtomIdsRequestDto atomIds, @PathVariable UUID capsuleId) {
        CapsuleResponseDto updatedCapsule = this.capsuleService.assignAtomToCapsule(capsuleId, atomIds);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atoms added to capsule successfully!")
                .errors(null)
                .data(Map.of("item", updatedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/removeAtom")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Remove a skill atom from capsule", description = "This end point allows admin to remove a skill atom from capsule")
    public ResponseEntity<ClientResponseFormatDto> removeAtomFromCapsule(
            @RequestParam UUID capsuleId, @RequestParam UUID atomId) {
        this.capsuleService.removeAtomFromCapsule(capsuleId, atomId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atom removed from capsule successfully!")
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/reorderAtom/{capsuleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Reorder new skill atom in capsule collection", description = "This end point allows admin " +
            "to shift atom sequence order in capsule however they want")
    public ResponseEntity<ClientResponseFormatDto> reorderAtom(
            @RequestBody AtomIdsRequestDto atomIds, @PathVariable UUID capsuleId) {
        CapsuleResponseDto updatedCapsule = this.capsuleService.reorderAtoms(capsuleId, atomIds.getAtomIds());
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atoms reordered in capsule successfully!")
                .errors(null)
                .data(Map.of("item", updatedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/assignTag/{capsuleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Assign new skill tag to capsule", description = "This end point allows admin to add new skill tag to capsule")
    public ResponseEntity<ClientResponseFormatDto> assignTagToCapsule(
            @RequestBody TagIdsRequestDto tagIds, @PathVariable UUID capsuleId) {
        CapsuleResponseDto updatedCapsule = this.capsuleService.assignTagToCapsule(capsuleId, tagIds);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tags added to capsule successfully!")
                .errors(null)
                .data(Map.of("item", updatedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/removeTag")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Remove a skill tag from capsule", description = "This end point allows admin to remove a skill tag from capsule")
    public ResponseEntity<ClientResponseFormatDto> removeTagFromCapsule(
            @RequestParam UUID capsuleId, @RequestParam UUID tagId) {
        this.capsuleService.removeTagFromCapsule(capsuleId, tagId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Tag removed from capsule successfully!")
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/assignCluster/{capsuleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Assign new skill cluster to capsule", description = "This end point allows admin to add new skill cluster to capsule")
    public ResponseEntity<ClientResponseFormatDto> assignClusterToCapsule(
            @RequestBody ClusterIdsRequestDto clusterIds, @PathVariable UUID capsuleId) {
        CapsuleResponseDto updatedCapsule = this.capsuleService.assignClusterToCapsule(capsuleId, clusterIds);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Clusters added to capsule successfully!")
                .errors(null)
                .data(Map.of("item", updatedCapsule))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/removeCluster")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Remove a skill cluster from capsule", description = "This end point allows admin to remove a skill cluster from capsule")
    public ResponseEntity<ClientResponseFormatDto> removeClusterFromCapsule(
            @RequestParam UUID capsuleId, @RequestParam UUID clusterId) {
        this.capsuleService.removeClusterFromCapsule(capsuleId, clusterId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Cluster removed from capsule successfully!")
                .errors(null)
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
