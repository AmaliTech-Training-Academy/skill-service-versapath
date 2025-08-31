package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;
import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteUpdateRequestDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.TrackIdsRequestDto;
import com.capstone.skill_service.service.RouteService;
import com.capstone.skill_service.util.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("talent-routes")
@Tag(name = "Talent routes Controller", description = "Manage all talent route's api")
public class RouteController {
    private final RouteService routeService;

    @PostMapping()
    @Operation(summary = "Create talent route", description = "This end point allows only admin to create a talent route")
    public ResponseEntity<ClientResponseFormatDto> createRoute(
            @Valid @RequestBody RouteRequestDto routeRequestDto
    ) {
        RouteResponseDto savedRoute = this.routeService.create(routeRequestDto);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Route created successfully!")
                .errors(null)
                .data(Map.of("item", savedRoute))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @Operation(summary = "Retrieve talent routes", description = "This end point allows only admin to fetch all talent routes")
    public ResponseEntity<ClientResponseFormatDto> fetchAllRoutes(Pageable pageable) {
        CustomPageResponse<RouteResponseDto> routes = this.routeService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Fetch all talent routes")
                .errors(null)
                .data(routes)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{routeId}")
    @Operation(summary = "Retrieve talent routes", description = "This end point allows only admin to fetch all talent routes")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleRoute(@PathVariable UUID routeId) {
        RouteWithTrackResponseDto route = this.routeService.getRouteWithTracks(routeId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Route retrieved successfully")
                .errors(null)
                .data(Map.of("item", route))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(name = "delete_route", path = "/{id}")
    @Operation(summary = "Delete Route",
            description = "The route is deleted using its id that is passed as parameter")
    public ResponseEntity<ClientResponseFormatDto> deleteRoute(@PathVariable UUID id){
        this.routeService.deleteById(id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Growth Route deleted successfully!")
                .errors(null)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{routeId}")
    @Operation(summary = "Update talent route", description = "This end point allows admin to update a talent route")
    public ResponseEntity<ClientResponseFormatDto> updateRoute(
            @Valid @RequestBody RouteUpdateRequestDto routeRequestDto, @PathVariable UUID routeId) {
        RouteResponseDto updatedRoute = this.routeService.partialUpdate(routeRequestDto, routeId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Growth Route updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedRoute))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    @Operation(summary = "Update talent route status", description = "This end point allows admin to update a talent route as a soft delete")
    public ResponseEntity<ClientResponseFormatDto> updateRouteStatus(
            @RequestParam UUID id, @RequestParam Status status) {
        RouteResponseDto updatedRoute = this.routeService.updateStatus(status, id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Growth Route status updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedRoute))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/assignTrack/{routeId}")
    @Operation(summary = "Assign new skill capsule to a talent route", description = "This end point allows admin to add new skill capsule to a talent route")
    public ResponseEntity<ClientResponseFormatDto> assignTrackToRoute(
            @RequestBody TrackIdsRequestDto atomIds, @PathVariable UUID routeId) {
        RouteResponseDto updatedRoute = this.routeService.assignTrackToRoute(routeId, atomIds);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Tracks added to a talent route successfully!")
                .errors(null)
                .data(Map.of("item", updatedRoute))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/removeTrack")
    @Operation(summary = "Remove a growth track from route", description = "This end point allows admin to remove a growth track from route")
    public ResponseEntity<ClientResponseFormatDto> removeTrackFromRoute(
            @RequestParam UUID routeId, @RequestParam UUID trackId) {
        RouteResponseDto updatedRoute = this.routeService.removeTrackFromRoute(routeId, trackId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Track removed from route successfully!")
                .errors(null)
                .data(Map.of("item", updatedRoute))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/reorderTrack/{routeId}")
    @Operation(summary = "Reorder new skill capsule in a talent route collection", description = "This end point allows admin " +
            "to shift capsule sequence order in a talent route however they want")
    public ResponseEntity<ClientResponseFormatDto> reorderTrack(
            @RequestBody TrackIdsRequestDto atomIds, @PathVariable UUID routeId) {
        RouteResponseDto updatedRoute = this.routeService.reorderTracks(routeId, atomIds.getTrackIds());
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .status(true)
                .message("Tracks reordered in route successfully!")
                .errors(null)
                .data(Map.of("item", updatedRoute))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
