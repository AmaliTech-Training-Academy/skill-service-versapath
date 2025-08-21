package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
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

}
