package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.capsule.CapsuleIdsRequestDto;
import com.capstone.skill_service.dto.track.TrackRequestDto;
import com.capstone.skill_service.dto.track.TrackResponseDto;
import com.capstone.skill_service.dto.track.TrackUpdateRequestDto;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.ClusterMapper;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.mapper.TrackMapper;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.TrackService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {
    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final TrackRepository trackRepository;
    private final TrackMapper trackMapper;


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

        int numberOfCapsulesInCapsule = trackEntity.getSkillCapsules().size();

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
            TrackCapsuleMappingEntity assignCapsuleToCapsule = TrackCapsuleMappingEntity.builder()
                    .capsule(capsule)
                    .growthTrack(trackEntity)
                    .sequenceOrder(numberOfCapsulesInCapsule + i + 1) // set default sequence order, when child size is 0, order starts from 1 else starts from size number + 1
                    .build();

            trackEntity.getSkillCapsules().add(assignCapsuleToCapsule);// add a single assignment to assigment collection
        }

    }

    @Override
    public Optional<GrowthTrackEntity> findByName(String name) {
        return this.trackRepository.findByName(name);
    }

    @Override
    public Optional<GrowthTrackEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public CustomPageResponse<TrackResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public TrackResponseDto getTrack(UUID id) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public TrackResponseDto partialUpdate(TrackUpdateRequestDto dto, UUID id) {
        return null;
    }

    @Override
    public TrackResponseDto updateStatus(Status status, UUID id) {
        return null;
    }

    @Override
    public TrackResponseDto assignCapsuleToTrack(UUID trackId, CapsuleIdsRequestDto dto) {
        return null;
    }

    @Override
    public TrackResponseDto removeCapsuleFromTrack(UUID trackId, UUID capsuleId) {
        return null;
    }

    @Override
    public TrackResponseDto reorderCapsules(UUID trackId, List<UUID> orderedCapsuleIds) {
        return null;
    }

}
