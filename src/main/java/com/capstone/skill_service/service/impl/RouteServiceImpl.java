package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteUpdateRequestDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.AtomMapper;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.RouteMapper;
import com.capstone.skill_service.mapper.TrackMapper;
import com.capstone.skill_service.messaging.PopulateSkillEvents;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.RouteService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.common.event.TalentRouteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private static final Logger logger = LoggerFactory.getLogger(RouteServiceImpl.class);
    private final TrackRepository trackRepository;
    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final TrackCapsuleMappingRepository trackCapsuleMappingRepository;
    private final CapsuleMapper capsuleMapper;
    private final TrackMapper trackMapper;
    private final AtomMapper atomMapper;
    private final CapsuleAtomMappingRepository capsuleAtomMappingRepository;
    private final PopulateSkillEvents populateSkillEvents;


    @Override
    @CacheEvict(value = "talentRouteList",  allEntries = true)
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
        if(dto.getStatus() == null){ // set default value
            routeEntity.setStatus(Status.ACTIVE);
        }

        addTrackToRoute(routeEntity, dto.getTrackIds()); // link talent route to all the talent route assigned to

        TalentRouteEntity savedTalentRoute = routeRepository.save(routeEntity);

        logger.info("Admin created skill track: {}", routeEntity.getName());

        //populate an event
        populateTalentRouteEvent(savedTalentRoute);

        return this.routeMapper.toDto(savedTalentRoute);

    }

    void populateTalentRouteEvent(TalentRouteEntity talentRoute){
        List<Map<UUID, Integer>> listOfTrackInTalentRoute = new ArrayList<>();

        for(RouteTrackMappingEntity mapping: talentRoute.getTracks()){
            Map<UUID, Integer> growthTrackWithSequenceOrder = new HashMap<>();
            growthTrackWithSequenceOrder.put(mapping.getGrowthTrack().getId(), mapping.getSequenceOrder());

            listOfTrackInTalentRoute.add(growthTrackWithSequenceOrder);
        }

        populateSkillEvents.populateTalentRoute(TalentRouteEvent.builder()
                .id(talentRoute.getId())
                .name(talentRoute.getName())
                .description(talentRoute.getDescription())
                .growthTracks(listOfTrackInTalentRoute)
                .build());

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
        List<RouteTrackMappingEntity> mappings = route.getTracks();

        if (mappings.isEmpty()) {
            RouteWithTrackResponseDto dto = routeMapper.toWithTrackDto(route);
            dto.setTracks(List.of()); // return empty list in growth track field
            return dto;
        }

        // fetch capsules in growth track
        List<UUID> trackIds = mappings.stream()
                .map(m -> m.getGrowthTrack().getId())
                .toList();
        // fetch each growth track with its capsule
        Map<UUID, List<CapsuleInSequenceOrderResponseDto>> capsuleByGrowthTrack = getCapsulesByGrowthTrack(trackIds);

        // fetch each growth track with its capsules
        List<TrackInSequenceOrderResponseDto> growthTrackInSequenceOrder = getGrowthTrackInSequenceOrder(mappings, capsuleByGrowthTrack);

        // build final response
        RouteWithTrackResponseDto dto = routeMapper.toWithTrackDto(route);
        dto.setTracks(growthTrackInSequenceOrder);
        return dto;
    }
    public Map<UUID, List<CapsuleInSequenceOrderResponseDto>> getCapsulesByGrowthTrack(List<UUID> trackIds){
        List<TrackCapsuleMappingEntity> mappings = trackCapsuleMappingRepository.findByTrackIdsWithCapsules(trackIds);

        // start fetching capsules' atoms
        List<UUID> capsuleIds = mappings.stream()
                .map(m -> m.getCapsule().getId())
                .toList();
        Map<UUID, List<AtomInSequenceOrderResponseDto>> atomsByCapsule = getAtomsByCapsule(capsuleIds);

        // group capsule by growth track
        return mappings.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getGrowthTrack().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                m -> {
                                    // inject atom in capsule as well
                                    CapsuleInSequenceOrderResponseDto dto = capsuleMapper.toInSequenceOrderDto(m);
                                    dto.setSkillAtoms(atomsByCapsule.getOrDefault(m.getCapsule().getId(), List.of()));
                                    return dto;
                                }, Collectors.toList())
                ));
    }

    public Map<UUID, List<AtomInSequenceOrderResponseDto>> getAtomsByCapsule(List<UUID> capsuleIds){
        List<CapsuleAtomMappingEntity> mappings = capsuleAtomMappingRepository.findByCapsuleIdsWithAtoms(capsuleIds);

        // group atoms by capsule
        return mappings.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCapsule().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(atomMapper::toInSequenceOrderDto, Collectors.toList())
                ));
    }

    public List<TrackInSequenceOrderResponseDto> getGrowthTrackInSequenceOrder(List<RouteTrackMappingEntity> mappings, Map<UUID, List<CapsuleInSequenceOrderResponseDto>> capsuleByGrowthTrack  ){
        return mappings.stream()
                .map(mapping -> {
                    GrowthTrackEntity track = mapping.getGrowthTrack();

                    TrackInSequenceOrderResponseDto dto = trackMapper.toInSequenceOrderDto(mapping);
                    dto.setSequenceOrder(mapping.getSequenceOrder()); // link the sequence order
                    dto.setCapsules(capsuleByGrowthTrack.getOrDefault(track.getId(), List.of()));

                    return dto;
                })
                .toList();
    }

    @Override
    @CacheEvict(value = "talentRouteList",  allEntries = true)
    public void deleteById(UUID id) {
        TalentRouteEntity route = findById(id)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );

        logger.info("Talent route {} deleted", route.getName());

        this.routeRepository.deleteById(id);
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "talentRouteList", allEntries = true), // to update the entire list
            put = @CachePut(value = "talentRoute", key = "#result.id") // Different cache name for individual track
    )
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
    @Caching(
            evict = @CacheEvict(value = "talentRouteList", allEntries = true), // to update the entire list
            put = @CachePut(value = "talentRoute", key = "#result.id") // Different cache name for individual track
    )
    public RouteResponseDto updateStatus(Status status, UUID id) {

        TalentRouteEntity route = findById(id)
                .orElseThrow( () -> new RouteNotFoundException("A track route provided doesn't exist")
                );
        route.setStatus(status);

        return this.routeMapper.toDto(this.routeRepository.save(route));

    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "talentRouteList", allEntries = true), // to update the entire list
            put = @CachePut(value = "talentRoute", key = "#result.id") // Different cache name for individual track
    )
    public RouteResponseDto assignTrackToRoute(UUID routeId, TrackIdsRequestDto dto) {

        TalentRouteEntity route = findById(routeId)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );

        addTrackToRoute(route, dto.getTrackIds()); // link route to all the growth track assigned to it

        logger.info("Admin added new growth track to talent route: {}", route.getName());

        return this.routeMapper.toDto(routeRepository.save(route));

    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "talentRouteList", allEntries = true), // to update the entire list
            put = @CachePut(value = "talentRoute", key = "#result.id") // Different cache name for individual track
    )
    public RouteResponseDto removeTrackFromRoute(UUID routeId, UUID trackId) {

        TalentRouteEntity route = findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("A talent route provided doesn't exist"));

        // find the growth track to remove from talent route
        RouteTrackMappingEntity growthTrackToRemove = route.getTracks().stream()
                .filter(mapping -> mapping.getGrowthTrack().getId().equals(trackId))
                .findFirst()
                .orElseThrow(() -> new TrackNotFoundException("A growth track provided doesn't exist in talent route"));

        route.getTracks().remove(growthTrackToRemove); // remove growth track from track collection

        //Rearrange the sequence order
        int order = 1;
        for (RouteTrackMappingEntity mapping : route.getTracks()) {
            mapping.setSequenceOrder(order++);
        }
        TalentRouteEntity savedTrack = routeRepository.save(route);

        // map growth track to response dto
        return this.routeMapper.toDto(savedTrack);
    }


    @Override
    @Caching(
            evict = @CacheEvict(value = "talentRouteList", allEntries = true), // to update the entire list
            put = @CachePut(value = "talentRoute", key = "#result.id") // Different cache name for individual track
    )
    public RouteResponseDto reorderTracks(UUID trackId, List<UUID> orderedTrackIds) {
        TalentRouteEntity route = findById(trackId)
                .orElseThrow( () -> new RouteNotFoundException("A talent route provided doesn't exist")
                );

        // check whether all the incoming growth tracks exist in talent route list in DB
        List<RouteTrackMappingEntity> existingTracks = route.getTracks(); // in database
        List<UUID> existingTrackIds = existingTracks.stream().map(m->m.getGrowthTrack().getId()).toList();

        Set<UUID> noneDuplicateOrderedTrackIds = new HashSet<>(orderedTrackIds);

        if(! new HashSet<>(existingTrackIds).containsAll(orderedTrackIds) || noneDuplicateOrderedTrackIds.size() != existingTracks.size()){
            throw new InvalidTrackIdsException("Invalid growth track for this talent route");
        }

        // Update sequence order
        for (int i = 0; i < orderedTrackIds.size(); i++) {
            UUID capsuleId = orderedTrackIds.get(i); // incoming capsule

            RouteTrackMappingEntity existingTrackMapping = existingTracks.stream()
                    .filter(m -> m.getGrowthTrack().getId().equals(capsuleId))
                    .findFirst()
                    .orElseThrow(() -> new TrackNotFoundException("A growth track provided doesn't exist in talent route"));

            existingTrackMapping.setSequenceOrder(i + 1); // update sequence order
        }
        return routeMapper.toDto(routeRepository.save(route));

    }
}
