package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.PaginationData;
import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleIdsRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.AtomMapper;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.TrackMapper;
import com.capstone.skill_service.messaging.PopulateSkillEvents;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.AwsFileUploadService;
import com.capstone.skill_service.service.PreSignedUrlService;
import com.capstone.skill_service.service.TrackService;
import com.capstone.skill_service.util.FileHelper;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.common.event.GrowthTrackEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final TrackRepository trackRepository;
    private final TrackMapper trackMapper;
    private final AtomMapper atomMapper;
    private final CapsuleMapper capsuleMapper;
    private final RouteTrackMappingRepository routeTrackMappingRepository;
    private final CapsuleAtomMappingRepository capsuleAtomMappingRepository;
    private final PopulateSkillEvents populateSkillEvents;
    private final AwsFileUploadService awsFileUploadService;
    private final PreSignedUrlService preSignedUrlService;

    @Override
    @CacheEvict(value = "growthTrackList",  allEntries = true)
    public TrackResponseDto create(TrackRequestDto dto, MultipartFile image) throws IOException {
        if(findByName(dto.getName()).isPresent()){
            throw new TrackExistsException(
                    String.format("A Growth Track with the name '%s' already exist",
                            dto.getName()));
        }
        GrowthTrackEntity trackEntity = this.trackMapper.toEntity(dto);

        trackEntity.setCreatedAt(LocalDateTime.now());
        trackEntity.setUpdatedAt(LocalDateTime.now());
        if(dto.getStatus() == null){ // set default value
            trackEntity.setStatus(Status.ACTIVE);
        }
        if(image != null){
            FileHelper.validateImage(image); // validate image
            trackEntity.setImage(awsFileUploadService.uploadFile(image)); //upload image
        }
        addCapsulesToTrack(trackEntity, dto.getCapsuleIds()); // link track to all the capsules assigned to

        GrowthTrackEntity savedTrack = trackRepository.save(trackEntity);
        TrackResponseDto response = this.trackMapper.toDto(savedTrack);

        // populate an event to create growth track
        populateGrowthTrackEvent(savedTrack, "create");

        logger.info("Admin created skill track: {}", trackEntity.getName());

        FileHelper.generatePresignedUrl(savedTrack, response, preSignedUrlService);

        return response;

    }

    void populateGrowthTrackEvent(GrowthTrackEntity savedTrack, String eventType){
        List<Map<UUID, Integer>> listOfCapsulesInGrowthTrack = new ArrayList<>();

        for(TrackCapsuleMappingEntity mapping: savedTrack.getSkillCapsules()){
            Map<UUID, Integer> capsuleWithSequenceOrder = new HashMap<>();
            capsuleWithSequenceOrder.put(mapping.getCapsule().getId(), mapping.getSequenceOrder());

            listOfCapsulesInGrowthTrack.add(capsuleWithSequenceOrder);
        }

        if(eventType.equalsIgnoreCase("delete")){
            populateSkillEvents.populateDeleteGrowthTrack(GrowthTrackEvent.builder()
                    .id(savedTrack.getId())
                    .name(savedTrack.getName())
                    .build());
        } else if (eventType.equalsIgnoreCase("update")) {
            populateSkillEvents.populateUpdateGrowthTrack(GrowthTrackEvent.builder()
                    .id(savedTrack.getId())
                    .name(savedTrack.getName())
                    .image(savedTrack.getImage())
                    .description(savedTrack.getDescription())
                    .skillCapsules(listOfCapsulesInGrowthTrack)
                    .build());
        } else if (eventType.equalsIgnoreCase("assignCapsule")) {
            populateSkillEvents.populateAssignGrowthTrack(GrowthTrackEvent.builder()
                    .id(savedTrack.getId())
                    .name(savedTrack.getName())
                    .image(savedTrack.getImage())
                    .description(savedTrack.getDescription())
                    .skillCapsules(listOfCapsulesInGrowthTrack)
                    .build());
        } else{
            populateSkillEvents.populateGrowthTrack(GrowthTrackEvent.builder()
                    .id(savedTrack.getId())
                    .name(savedTrack.getName())
                    .image(savedTrack.getImage())
                    .description(savedTrack.getDescription())
                    .skillCapsules(listOfCapsulesInGrowthTrack)
                    .build());
        }

    }


    void addCapsulesToTrack(GrowthTrackEntity trackEntity, List<UUID> capsuleIds){
        if (trackEntity.getSkillCapsules() == null) {
            trackEntity.setSkillCapsules(new ArrayList<>());
        }

        int numberOfCapsulesInTrack = trackEntity.getSkillCapsules().size();

        for(int i=0; i<capsuleIds.size(); i++){
            UUID capsuleId=capsuleIds.get(i); //get capsule id

            SkillCapsuleEntity capsule = this.capsuleRepository.findById(capsuleId)
                    .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                    );

            boolean alreadyAssignedToCapsule = trackEntity.getSkillCapsules().stream()
                    .anyMatch(mapping -> mapping.getCapsule().getId().equals(capsuleId));

            if(alreadyAssignedToCapsule){
                throw new AlreadyAssignedException("Capsule is already assigned to this track");
            }

            // assign capsule to track
            TrackCapsuleMappingEntity assignCapsuleToTrack = TrackCapsuleMappingEntity.builder()
                    .capsule(capsule)
                    .growthTrack(trackEntity)
                    .sequenceOrder(numberOfCapsulesInTrack + i + 1) // set default sequence order, when child size is 0, order starts from 1 else starts from size number + 1
                    .build();

            trackEntity.getSkillCapsules().add(assignCapsuleToTrack);// add a single capsule to growth track collection
        }

    }

    @Override
    public Optional<GrowthTrackEntity> findByName(String name) {
        return this.trackRepository.findByName(name);
    }

    @Override
    public Optional<GrowthTrackEntity> findById(UUID id) {
        return this.trackRepository.findById(id);
    }

    @Override
    @Cacheable("growthTrackList")
    public CustomPageResponse<TrackOnlyResponseDto> findAll(Pageable pageable) {
        Page<TrackOnlyResponseDto> growthTracks = this.trackRepository.findTrackWithCapsuleCount(pageable);

        for(TrackOnlyResponseDto growthTrack: growthTracks){
            // generate presigned url
            String imageUrl = FileHelper.getGeneratedPresignedUrl(this.trackMapper.toEntity(growthTrack),
                    preSignedUrlService);
            growthTrack.setImage(imageUrl);
        }

        logger.info("Growth tracks list is fetched");

        return CustomPageResponse.<TrackOnlyResponseDto>builder()
                .items(growthTracks.getContent())
                .pagination(PaginationData.builder()
                    .page(growthTracks.getNumber())
                    .size(growthTracks.getSize())
                    .totalElements(growthTracks.getTotalElements())
                    .totalPages(growthTracks.getTotalPages())
                    .hasNext(growthTracks.hasNext())
                    .hasPrevious(growthTracks.hasPrevious())
                    .build())
                .build();
    }

    @Override
    @Cacheable(value = "track", key = "#trackId")
    public TrackWithCapsuleResponseDto getTrackWithCapsules(UUID trackId) {
        GrowthTrackEntity track = trackRepository.findByIdWithCapsules(trackId)
                .orElseThrow(() -> new TrackNotFoundException("A Growth Track not found"));

        // generate presigned url
        String imageUrl = FileHelper.getGeneratedPresignedUrl(track,
                preSignedUrlService);
        track.setImage(imageUrl);

        // get mappings (capsule and sequence order)
        List<TrackCapsuleMappingEntity> mappings = track.getSkillCapsules();
        if (mappings.isEmpty()) {
            TrackWithCapsuleResponseDto dto = trackMapper.toWithCapsuleDto(track);
            dto.setCapsules(List.of()); // return empty list in capsule field
            return dto;
        }


        // fetch capsules in growth track
        List<UUID> capsuleIds = mappings.stream()
                .map(m -> m.getCapsule().getId())
                .toList();
        // fetch each capsule with its atoms
        Map<UUID, List<AtomInSequenceOrderResponseDto>> atomsByCapsule = getAtomsByCapsule(capsuleIds);
        List<CapsuleInSequenceOrderResponseDto> capsuleInSequenceOrder=getCapsuleInSequenceOrder(mappings,atomsByCapsule);

        // build final response
        TrackWithCapsuleResponseDto dto = trackMapper.toWithCapsuleDto(track);
        dto.setCapsules(capsuleInSequenceOrder);

        return dto;
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
    public List<CapsuleInSequenceOrderResponseDto> getCapsuleInSequenceOrder(List<TrackCapsuleMappingEntity> mappings, Map<UUID, List<AtomInSequenceOrderResponseDto>> atomsByCapsule ){
        return mappings.stream()
                .map(mapping -> {
                    SkillCapsuleEntity capsule = mapping.getCapsule();

                    CapsuleInSequenceOrderResponseDto dto = capsuleMapper.toInSequenceOrderDto(mapping);
                    dto.setSequenceOrder(mapping.getSequenceOrder()); // link the sequence order
                    dto.setSkillAtoms(atomsByCapsule.getOrDefault(capsule.getId(), List.of()));

                    return dto;
                })
                .toList();
    }

    @Override
    public TrackResponseDto getTrack(UUID id) {
        GrowthTrackEntity track = findById(id)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );

        logger.info("Growth track {} retrieved", track.getName());

        return this.trackMapper.toDto(track);
    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "growthTrackList", allEntries = true), // to update the entire list
            @CacheEvict(value = "track", key = "#id") // evict single track
        }
    )
    public void deleteById(UUID id) {
        GrowthTrackEntity track = findById(id)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );
        // first delete all the reference
        List<RouteTrackMappingEntity> mappings = routeTrackMappingRepository.findByTrackId(id);
        routeTrackMappingRepository.deleteAll(mappings);

        // populate event to delete growth track
        populateGrowthTrackEvent(track, "delete");

        logger.info("Growth track {} deleted", track.getName());

        this.trackRepository.deleteById(id);
    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "growthTrackList", allEntries = true), // to update the entire list
            @CacheEvict(value = "track", key = "#dto.getTrackId()") // evict single track
        }
    )
    public TrackResponseDto partialUpdate(TrackUpdateRequestDto dto, MultipartFile image) throws IOException {
        GrowthTrackEntity track = findById(dto.getTrackId())
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );
        if(dto.getName() != null){
            if(findByName(dto.getName()).isPresent()){
                throw new TrackNotFoundException(
                        String.format("A Growth track with the name '%s' already exist",
                                dto.getName()));
            }
            track.setName(dto.getName());
        }
        if(dto.getDescription() != null){
            track.setDescription(dto.getDescription());
        }
        if(dto.getDescription() != null){
            track.setDescription(dto.getDescription());
        }

        if(dto.getEstimatedMonths() != track.getEstimatedMonths()){
            if(dto.getEstimatedMonths() < 0 || dto.getEstimatedMonths() > 36){
                throw new InvalidTimeException("Estimated months is invalid, must be between 1 and 36");
            }
            track.setEstimatedMonths(dto.getEstimatedMonths());
        }

        if(dto.getStatus() != null){
            track.setStatus(dto.getStatus());
        }
        if(dto.getCapsuleIds() != null){
            addCapsulesToTrack(track, dto.getCapsuleIds());
        }
        if(image != null){
            FileHelper.validateImage(image); // validate image
            track.setImage(awsFileUploadService.uploadFile(image)); //upload image
        }

        track.setUpdatedAt(LocalDateTime.now());

        GrowthTrackEntity savedTrack = this.trackRepository.save(track);
        TrackResponseDto response = this.trackMapper.toDto(savedTrack);

        // populate event to update growth track
        populateGrowthTrackEvent(savedTrack, "update");

        FileHelper.generatePresignedUrl(savedTrack, response, preSignedUrlService);

        logger.info("Growth track {} updated", track.getName());

        return response;
    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "growthTrackList", allEntries = true), // to update the entire list
            @CacheEvict(value = "track", key = "#id") // evict single track
        }
    )
    public TrackResponseDto updateStatus(Status status, UUID id) {
        GrowthTrackEntity track = findById(id)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );
        track.setStatus(status);

        return this.trackMapper.toDto(this.trackRepository.save(track));

    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "growthTrackList", allEntries = true), // to update the entire list
            @CacheEvict(value = "track", key = "#trackId") // evict single track
        }
    )
    public TrackResponseDto assignCapsuleToTrack(UUID trackId, CapsuleIdsRequestDto dto) {
        GrowthTrackEntity track = findById(trackId)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );

        addCapsulesToTrack(track, dto.getCapsuleIds()); // link track to all the capsules assigned to it

        GrowthTrackEntity savedTrack = trackRepository.save(track);
        logger.info("Admin added new skill capsule to growth track: {}", track.getName());

        // publish event to assign new capsule
        populateGrowthTrackEvent(savedTrack, "assignCapsule");

        return this.trackMapper.toDto(savedTrack);

    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "growthTrackList", allEntries = true), // to update the entire list
            @CacheEvict(value = "track", key = "#trackId") // evict single track
        }
    )
    public void removeCapsuleFromTrack(UUID trackId, UUID capsuleId) {
        GrowthTrackEntity trackEntity = findById(trackId)
                .orElseThrow(() -> new TrackNotFoundException("A growth track provided doesn't exist"));

        // find the capsule to remove from growth track
        TrackCapsuleMappingEntity capsuleToRemove = trackEntity.getSkillCapsules().stream()
                .filter(mapping -> mapping.getCapsule().getId().equals(capsuleId))
                .findFirst()
                .orElseThrow(() -> new CapsuleNotFoundException("A skill capsule provided doesn't exist in growth track"));

        trackEntity.getSkillCapsules().remove(capsuleToRemove); // remove capsule from growth track collection

        //Rearrange the sequence order
        int order = 1;
        for (TrackCapsuleMappingEntity mapping : trackEntity.getSkillCapsules()) {
            mapping.setSequenceOrder(order++);
        }
        trackRepository.save(trackEntity);

    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "growthTrackList", allEntries = true), // to update the entire list
            @CacheEvict(value = "track", key = "#trackId") // evict single track
        }
    )
    public TrackResponseDto reorderCapsules(UUID trackId, List<UUID> orderedCapsuleIds) {
        GrowthTrackEntity trackEntity = findById(trackId)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );

        // check whether all the incoming capsules exist in track list in DB
        List<TrackCapsuleMappingEntity> existingCapsules = trackEntity.getSkillCapsules(); // in database
        List<UUID> existingCapsuleIds = existingCapsules.stream().map(m->m.getCapsule().getId()).toList();

        Set<UUID> noneDuplicateOrderedCapsuleIds = new HashSet<>(orderedCapsuleIds);

        if(! new HashSet<>(existingCapsuleIds).containsAll(orderedCapsuleIds) || noneDuplicateOrderedCapsuleIds.size() != existingCapsules.size()){
            throw new InvalidCapsuleIdsException("Invalid skill capsule for this track");
        }

        // Update sequence order
        for (int i = 0; i < orderedCapsuleIds.size(); i++) {
            UUID capsuleId = orderedCapsuleIds.get(i); // incoming capsule

            TrackCapsuleMappingEntity existingCapsuleMapping = existingCapsules.stream()
                    .filter(m -> m.getCapsule().getId().equals(capsuleId))
                    .findFirst()
                    .orElseThrow(() -> new CapsuleNotFoundException("A skill capsule provided doesn't exist in track"));

            existingCapsuleMapping.setSequenceOrder(i + 1); // update sequence order
        }
        return trackMapper.toDto(trackRepository.save(trackEntity));

    }

    @Override
    public CustomPageResponse<TrackOnlyResponseDto> filterByName(String name, Pageable pageable) {
        Page<TrackOnlyResponseDto> growthTracks;

        if(name == null || name.trim().isEmpty()){
            growthTracks = this.trackRepository.findTrackWithCapsuleCount(PageRequest.of(0, 20));
        }else{
            growthTracks = this.trackRepository.filterByNameContainingIgnoreCase(name, pageable);
        }

        for(TrackOnlyResponseDto growthTrack: growthTracks){
            // generate presigned url
            String imageUrl = FileHelper.getGeneratedPresignedUrl(this.trackMapper.toEntity(growthTrack),
                    preSignedUrlService);
            growthTrack.setImage(imageUrl);
        }

        logger.info("Growth tracks list is filtered");

        return CustomPageResponse.<TrackOnlyResponseDto>builder()
                .items(growthTracks.getContent())
                .pagination(PaginationData.builder()
                        .page(growthTracks.getNumber())
                        .size(growthTracks.getSize())
                        .totalElements(growthTracks.getTotalElements())
                        .totalPages(growthTracks.getTotalPages())
                        .hasNext(growthTracks.hasNext())
                        .hasPrevious(growthTracks.hasPrevious())
                        .build())
                .build();
    }

}
