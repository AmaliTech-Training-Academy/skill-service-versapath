package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteUpdateRequestDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.RouteMapper;
import com.capstone.skill_service.mapper.TrackMapper;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.RouteService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final TrackRepository trackRepository;
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final TrackMapper trackMapper;
    private final TrackCapsuleMappingRepository trackCapsuleMappingRepository;
    private final CapsuleAtomMappingRepository capsuleAtomMappingRepository;


    @Override
    public RouteResponseDto create(RouteRequestDto dto) {
        if(findByName(dto.getName()).isPresent()){
            throw new RouteExistsException(
                    String.format("A talent route with the name '%s' already exist",
                            dto.getName()));
        }
        if(findByRoleName(dto.getRoleName()).isPresent()){
            throw new RouteExistsException(
                    String.format("A role name '%s' already exist",
                            dto.getRoleName()));
        }
        TalentRouteEntity routeEntity = this.routeMapper.toEntity(dto);

        routeEntity.setCreatedAt(LocalDateTime.now());
        routeEntity.setUpdatedAt(LocalDateTime.now());
        routeEntity.setStatus(Status.ACTIVE);
        addTrackToRoute(routeEntity, dto.getTrackIds()); // link talent route to all the talent route assigned to


        logger.info("Admin created skill track: {}", routeEntity.getName());

        return this.routeMapper.toDto(routeRepository.save(routeEntity));

    }

    void addTrackToRoute(TalentRouteEntity routeEntity, List<UUID> trackIds){
        if (routeEntity.getTracks() == null) {
            routeEntity.setTracks(new ArrayList<>());
        }

        int numberOfTracksInRoute = routeEntity.getTracks().size();

        for(int i=0; i<trackIds.size(); i++){
            UUID trackId=trackIds.get(i); //get track id

            GrowthTrackEntity track = this.trackRepository.findById(trackId)
                    .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                    );

            boolean alreadyAssignedToTrack = routeEntity.getTracks().stream()
                    .anyMatch(mapping -> mapping.getGrowthTrack().getId().equals(trackId));

            if(alreadyAssignedToTrack){
                throw new AlreadyAssignedException("Growth track is already assigned to this talent route");
            }

            // assign growth track to a talent route
            RouteTrackMappingEntity assignTrackToRoute = RouteTrackMappingEntity.builder()
                    .growthTrack(track)
                    .talentRoute(routeEntity)
                    .sequenceOrder(numberOfTracksInRoute + i + 1) // set default sequence order, when child size is 0, order starts from 1 else starts from size number + 1
                    .build();

            routeEntity.getTracks().add(assignTrackToRoute);// add a single capsule to talent route collection
        }

    }

    @Override
    public Optional<TalentRouteEntity> findByName(String name) {
        return this.routeRepository.findByName(name);
    }

    @Override
    public Optional<TalentRouteEntity> findByRoleName(String roleName) {
        return this.routeRepository.findByRoleName(roleName);
    }

    @Override
    public Optional<TalentRouteEntity> findById(UUID id) {

        return this.routeRepository.findById(id);
    }

    @Override
    @Cacheable("talentRouteList")
    public CustomPageResponse<RouteResponseDto> findAll(Pageable pageable) {

        Page<TalentRouteEntity> routeList = this.routeRepository.findAllWithGrowthTrack(pageable);
        Page<RouteResponseDto> routes = routeList.map(this.routeMapper::toDto);

        logger.info("Talent route list is fetched");

        return CustomPageResponse.<RouteResponseDto>builder()
                .items(routes.getContent())
                .page(routes.getNumber())
                .size(routes.getSize())
                .totalElements(routes.getTotalElements())
                .totalPages(routes.getTotalPages())
                .hasNext(routes.hasNext())
                .hasPrevious(routes.hasPrevious())
                .build();
    }

    @Override
    public RouteResponseDto getRoute(UUID id) {

        TalentRouteEntity route = findById(id)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );

        logger.info("Talent route {} retrieved", route.getName());

        return this.routeMapper.toDto(route);
    }

    @Override
    public RouteWithTrackResponseDto getRouteWithTracks(UUID routeId) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public RouteResponseDto partialUpdate(RouteUpdateRequestDto dto, UUID id) {
        return null;
    }

    @Override
    public RouteResponseDto updateStatus(Status status, UUID id) {
        return null;
    }

    @Override
    public RouteResponseDto assignTrackToRoute(UUID routeId, TrackIdsRequestDto dto) {
        return null;
    }

    @Override
    public RouteResponseDto removeTrackFromRoute(UUID routeId, UUID trackId) {
        return null;
    }

    @Override
    public RouteResponseDto reorderTracks(UUID routeId, List<UUID> orderedTrackIds) {
        return null;
    }
}
