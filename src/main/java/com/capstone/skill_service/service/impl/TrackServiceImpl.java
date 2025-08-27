package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleIdsRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.track.TrackRequestDto;
import com.capstone.skill_service.dto.track.TrackResponseDto;
import com.capstone.skill_service.dto.track.TrackUpdateRequestDto;
import com.capstone.skill_service.dto.track.TrackWithCapsuleResponseDto;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.AtomMapper;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.TrackMapper;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.TrackService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
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
public class TrackServiceImpl implements TrackService {
    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final AtomMapper atomMapper;
    private final TrackRepository trackRepository;
    private final TrackMapper trackMapper;
    private final TrackCapsuleMappingRepository trackCapsuleMappingRepository;
    private final CapsuleAtomMappingRepository capsuleAtomMappingRepository;

    @Override
    public TrackResponseDto create(TrackRequestDto dto) {
        if(findByName(dto.getName()).isPresent()){
            throw new TrackExistsException(
                    String.format("A Growth Track with the name '%s' already exist",
                            dto.getName()));
        }
        GrowthTrackEntity trackEntity = this.trackMapper.toEntity(dto);

        trackEntity.setCreatedAt(LocalDateTime.now());
        trackEntity.setUpdatedAt(LocalDateTime.now());
        trackEntity.setStatus(Status.ACTIVE);
        addCapsulesToTrack(trackEntity, dto.getCapsuleIds()); // link track to all the capsules assigned to


        logger.info("Admin created skill track: {}", trackEntity.getName());

        return this.trackMapper.toDto(trackRepository.save(trackEntity));

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
    public CustomPageResponse<TrackResponseDto> findAll(Pageable pageable) {
        Page<GrowthTrackEntity> growthTrackList = this.trackRepository.findAllWithSkillCapsule(pageable);
        Page<TrackResponseDto> growthTracks = growthTrackList.map(this.trackMapper::toDto);

        logger.info("Growth tracks list is fetched");

        return CustomPageResponse.<TrackResponseDto>builder()
                .items(growthTracks.getContent())
                .page(growthTracks.getNumber())
                .size(growthTracks.getSize())
                .totalElements(growthTracks.getTotalElements())
                .totalPages(growthTracks.getTotalPages())
                .hasNext(growthTracks.hasNext())
                .hasPrevious(growthTracks.hasPrevious())
                .build();
    }

    @Override
    @Cacheable(value = "growthTrack", key = "#trackId")
    public TrackWithCapsuleResponseDto getTrackWithCapsules(UUID trackId) {
        GrowthTrackEntity track = trackRepository.findByIdWithCapsules(trackId)
                .orElseThrow(() -> new TrackNotFoundException("A Growth Track not found"));

        // get mappings (capsule and sequence order)
        List<TrackCapsuleMappingEntity> mappings =
                trackCapsuleMappingRepository.findByTrackIdsWithCapsules(trackId);

        if (mappings.isEmpty()) {
            TrackWithCapsuleResponseDto dto = trackMapper.toWithCapsuleDto(track);
            dto.setCapsules(List.of()); // return empty list in capsule field
            return dto;
        }
        //TODO

        return null;
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
    public void deleteById(UUID id) {
        GrowthTrackEntity track = findById(id)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );

        logger.info("Growth track {} deleted", track.getName());

        this.capsuleRepository.deleteById(id);
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "trackList", allEntries = true), // to update the entire list
            put = @CachePut(value = "track", key = "#result.id") // Different cache name for individual track
    )
    public TrackResponseDto partialUpdate(TrackUpdateRequestDto dto, UUID id) {
        GrowthTrackEntity track = findById(id)
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
            track.setEstimatedMonths(dto.getEstimatedMonths());
        }
        if(dto.getStatus() != null){
            track.setStatus(dto.getStatus());
        }
        if(dto.getCapsuleIds() != null){
            addCapsulesToTrack(track, dto.getCapsuleIds());
        }

        track.setUpdatedAt(LocalDateTime.now());

        logger.info("Growth track {} updated", track.getName());

        return this.trackMapper.toDto(this.trackRepository.save(track));
    }

    @Override
    public TrackResponseDto updateStatus(Status status, UUID id) {
        GrowthTrackEntity track = findById(id)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );
        track.setStatus(status);

        return this.trackMapper.toDto(this.trackRepository.save(track));

    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "trackList", allEntries = true), // to update the entire list
            put = @CachePut(value = "track", key = "#result.id") // Different cache name for individual track
    )
    public TrackResponseDto assignCapsuleToTrack(UUID trackId, CapsuleIdsRequestDto dto) {
        GrowthTrackEntity track = findById(trackId)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );

        addCapsulesToTrack(track, dto.getCapsuleIds()); // link track to all the capsules assigned to it

        logger.info("Admin added new skill capsule to growth track: {}", track.getName());

        return this.trackMapper.toDto(trackRepository.save(track));

    }

    @Override
    public TrackResponseDto removeCapsuleFromTrack(UUID trackId, UUID capsuleId) {
        //TODO
        return null;
    }

    @Override
    public TrackResponseDto reorderCapsules(UUID trackId, List<UUID> orderedCapsuleIds) {
        GrowthTrackEntity trackEntity = findById(trackId)
                .orElseThrow( () -> new TrackNotFoundException("A growth track provided doesn't exist")
                );

        // check whether all the incoming capsules exist in track list in DB
        List<TrackCapsuleMappingEntity> existingCapsules = trackEntity.getSkillCapsules(); // in database
        List<UUID> existingCapsuleIds = existingCapsules.stream().map(m->m.getCapsule().getId()).toList();

        Set<UUID> noneDuplicateOrderedCapsuleIds = new HashSet<>(orderedCapsuleIds);

        if(! new HashSet<>(existingCapsuleIds).containsAll(orderedCapsuleIds) || noneDuplicateOrderedCapsuleIds.size() != orderedCapsuleIds.size()){
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

}
