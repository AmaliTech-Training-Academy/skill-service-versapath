package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteUpdateRequestDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.RouteMapper;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.RouteService;
import com.capstone.skill_service.service.TrackService;
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
    private final TrackRepository trackRepository;
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final RouteTrackMappingRepository routeTrackMappingRepository;
    private final TrackService trackService;



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
    @Cacheable(value = "talentRoute", key = "#routeId")
    public RouteWithTrackResponseDto getRouteWithTracks(UUID routeId) {
        TalentRouteEntity route = routeRepository.findByIdWithGrowthTracks(routeId)
                .orElseThrow(() -> new RouteNotFoundException("A Talent Route not found"));

        // get mappings (growth track and sequence order)
        List<RouteTrackMappingEntity> mappings =
                routeTrackMappingRepository.findByTrackIdsWithCapsules(routeId);

        if (mappings.isEmpty()) {
            RouteWithTrackResponseDto dto = routeMapper.toWithTrackDto(route);
            dto.setTracks(List.of()); // return empty list in growth track field
            return dto;
        }

        // fetch each growth track with its capsules
        List<TrackInSequenceOrderResponseDto> growthTrackInSequenceOrder = getGrowthTrackInSequenceOrder(mappings);

        // build final response
        RouteWithTrackResponseDto dto = routeMapper.toWithTrackDto(route);
        dto.setTracks(growthTrackInSequenceOrder);
        return dto;
    }

    public List<TrackInSequenceOrderResponseDto> getGrowthTrackInSequenceOrder(List<RouteTrackMappingEntity> mappings ){
        return mappings.stream()
                .map(mapping -> {
                    GrowthTrackEntity track = mapping.getGrowthTrack();

                    // get each growth track from growth track service
                    TrackWithCapsuleResponseDto trackWithCapsules =
                            trackService.getTrackWithCapsules(track.getId());

                    // wrap in dto that is in routeResponseDto
                    TrackInSequenceOrderResponseDto dto = new TrackInSequenceOrderResponseDto();
                    dto.setId(trackWithCapsules.getId());
                    dto.setName(trackWithCapsules.getName());
                    dto.setDescription(trackWithCapsules.getDescription());
                    dto.setEstimatedMonths(trackWithCapsules.getEstimatedMonths());
                    dto.setStatus(trackWithCapsules.getStatus());
                    dto.setCapsules(trackWithCapsules.getCapsules());
                    dto.setSequenceOrder(mapping.getSequenceOrder());
                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        TalentRouteEntity route = findById(id)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );

        logger.info("Talent route {} deleted", route.getName());

        this.routeRepository.deleteById(id);
    }

    @Override
    public RouteResponseDto partialUpdate(RouteUpdateRequestDto dto, UUID id) {
        TalentRouteEntity route = findById(id)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );
        if(dto.getName() != null){
            if(findByName(dto.getName()).isPresent()){
                throw new RouteNotFoundException(
                        String.format("A talent route with the name '%s' already exist",
                                dto.getName()));
            }
            route.setName(dto.getName());
        }
        if(dto.getRoleName() != null){
            if(findByRoleName(dto.getRoleName()).isPresent()){
                throw new RouteNotFoundException(
                        String.format("A role with the name '%s' already exist",
                                dto.getName()));
            }
            route.setName(dto.getName());
        }
        if(dto.getDescription() != null){
            route.setDescription(dto.getDescription());
        }
        if(dto.getDescription() != null){
            route.setDescription(dto.getDescription());
        }

        if(dto.getStatus() != null){
            route.setStatus(dto.getStatus());
        }
        if(dto.getGrowthTrackIds() != null){
            addTrackToRoute(route, dto.getGrowthTrackIds());
        }

        route.setUpdatedAt(LocalDateTime.now());

        logger.info("Talent route {} updated", route.getName());

        return this.routeMapper.toDto(this.routeRepository.save(route));

    }

    @Override
    public RouteResponseDto updateStatus(Status status, UUID id) {

        TalentRouteEntity route = findById(id)
                .orElseThrow( () -> new RouteNotFoundException("A track route provided doesn't exist")
                );
        route.setStatus(status);

        return this.routeMapper.toDto(this.routeRepository.save(route));

    }

    @Override
    public RouteResponseDto assignTrackToRoute(UUID routeId, TrackIdsRequestDto dto) {

        TalentRouteEntity route = findById(routeId)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );

        addTrackToRoute(route, dto.getTrackIds()); // link route to all the growth track assigned to it

        logger.info("Admin added new growth track to talent route: {}", route.getName());

        return this.routeMapper.toDto(routeRepository.save(route));

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
