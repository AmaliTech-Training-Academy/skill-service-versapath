package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomIdsRequestDto;
import com.capstone.skill_service.dto.capsule.*;
import com.capstone.skill_service.dto.cluster.ClusterIdsRequestDto;
import com.capstone.skill_service.dto.tag.TagIdsRequestDto;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.util.Status;
import org.common.event.UpdateSkillEvent;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CapsuleService {
    CapsuleResponseDto create(CapsuleRequestDto dto);
    Optional<SkillCapsuleEntity> findByName(String name);
    CustomPageResponse<CapsuleOnlyResponseDto> findAll(Pageable pageable);
    Optional<SkillCapsuleEntity> findById(UUID id);
    CapsuleResponseDto getCapsule(UUID id);
    void deleteById(UUID id);
    CapsuleResponseDto partialUpdate(CapsuleUpdateRequestDto dto, UUID id);
    CapsuleResponseDto updateStatus(Status status, UUID id);
    CapsuleResponseDto assignAtomToCapsule(UUID capsuleId, AtomIdsRequestDto dto);
    void removeAtomFromCapsule(UUID capsuleId, UUID atomId);
    CapsuleResponseDto reorderAtoms(UUID capsuleId, List<UUID> orderedAtomIds);
    CapsuleResponseDto assignTagToCapsule(UUID capsuleId, TagIdsRequestDto dto);
    void removeTagFromCapsule(UUID capsuleId, UUID tagId);
    CapsuleResponseDto assignClusterToCapsule(UUID capsuleId, ClusterIdsRequestDto dto);
    void removeClusterFromCapsule(UUID capsuleId, UUID clusterId);
    CapsuleWithDetailsResponseDto getCapsuleWithDetails(UUID capsuleId);

    void updateSkillWithMoodleData(UpdateSkillEvent updateSkillEvent);
}
