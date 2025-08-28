package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterUpdateRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterWithCapsuleResponseDto;
import com.capstone.skill_service.service.ClusterService;
import com.capstone.skill_service.util.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("clusters")
@Tag(name = "Clusters Controller", description = "Manage all skill cluster's api")
public class ClusterController {
    private final ClusterService clusterService;

    @PostMapping()
    @Operation(summary = "Create skill cluster", description = "This end point allows only admin to create a skill cluster")
    public ResponseEntity<ClientResponseFormatDto> createCluster(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image
    )throws IOException {
        ClusterRequestDto clusterRequestDto = ClusterRequestDto.builder()
                .name(name)
                .type(type)
                .description(description)
                .build();
        ClusterResponseDto savedCluster = this.clusterService.create(clusterRequestDto, image);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Cluster created successfully!")
                .errors(null)
                .data(savedCluster)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @Operation(summary = "Retrieve skill clusters", description = "This end point allows only admin to fetch all skill clusters")
    public ResponseEntity<ClientResponseFormatDto> fetchAllClusters(Pageable pageable) {
        CustomPageResponse<ClusterResponseDto> clusters = this.clusterService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Fetch all clusters")
                .errors(null)
                .data(clusters)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{clusterId}")
    @Operation(summary = "Retrieve skill clusters by Id", description = "This end point allows only admin to fetch a skill clusters by id")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleCluster(@PathVariable UUID clusterId) {
        ClusterResponseDto cluster = this.clusterService.getCluster(clusterId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Cluster retrieved successfully")
                .errors(null)
                .data(cluster)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(name = "delete_cluster", path = "/{id}")
    @Operation(summary = "Delete Cluster",
            description = "The cluster is delete using its id that is passed as parameter")
    public ResponseEntity<ClientResponseFormatDto> deleteCluster(@PathVariable UUID id){
        this.clusterService.deleteById(id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Cluster deleted successfully!")
                .errors(null)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{clusterId}")
    @Operation(summary = "Update skill cluster", description = "This end point allows admin to update a skill cluster")
    public ResponseEntity<ClientResponseFormatDto> updateCluster(
            @Valid @RequestBody ClusterUpdateRequestDto clusterRequestDto, @PathVariable UUID clusterId) {
        ClusterResponseDto updatedCluster = this.clusterService.partialUpdate(clusterRequestDto, clusterId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Cluster updated successfully!")
                .errors(null)
                .data(updatedCluster)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    @Operation(summary = "Update skill cluster status", description = "This end point allows admin to update a skill cluster as a soft delete")
    public ResponseEntity<ClientResponseFormatDto> updateClusterStatus(
            @RequestParam UUID id, @RequestParam Status status) {
        ClusterResponseDto updatedCluster = this.clusterService.updateStatus(status, id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Cluster status updated successfully!")
                .errors(null)
                .data(updatedCluster)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{clusterId}/capsules")
    @Operation(summary = "Retrieve skill clusters by Id", description = "This end point allows only admin to fetch a skill clusters by id")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleClusterWithCapsules(@PathVariable UUID clusterId) {
        ClusterWithCapsuleResponseDto cluster = this.clusterService.getClusterWithCapsules(clusterId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Cluster retrieved successfully")
                .errors(null)
                .data(cluster)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
