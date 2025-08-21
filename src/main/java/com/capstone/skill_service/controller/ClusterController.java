package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterUpdateRequestDto;
import com.capstone.skill_service.service.ClusterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody ClusterRequestDto clusterRequestDto
    ) {
        ClusterResponseDto savedCluster = this.clusterService.create(clusterRequestDto);
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
    @Operation(summary = "Retrieve skill clusters", description = "This end point allows only admin to fetch all skill clusters")
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
}
