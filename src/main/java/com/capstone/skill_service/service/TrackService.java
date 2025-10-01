package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.capsule.CapsuleIdsRequestDto;
import com.capstone.skill_service.dto.track.*;
import com.capstone.skill_service.model.GrowthTrackEntity;
import com.capstone.skill_service.util.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackService {
    TrackResponseDto create(TrackRequestDto dto, MultipartFile image) throws IOException;
    Optional<GrowthTrackEntity> findByName(String name);
    Optional<GrowthTrackEntity> findById(UUID id);
    CustomPageResponse<TrackOnlyResponseDto> findAll(Pageable pageable);
    TrackResponseDto getTrack(UUID id);
    TrackWithCapsuleResponseDto getTrackWithCapsules(UUID trackId);
    void deleteById(UUID id);
    TrackResponseDto partialUpdate(TrackUpdateRequestDto dto, MultipartFile image) throws IOException;
    TrackResponseDto updateStatus(Status status, UUID id);
    TrackResponseDto assignCapsuleToTrack(UUID trackId, CapsuleIdsRequestDto dto);
    void removeCapsuleFromTrack(UUID trackId, UUID capsuleId);
    TrackResponseDto reorderCapsules(UUID trackId, List<UUID> orderedCapsuleIds);
    CustomPageResponse<TrackOnlyResponseDto> filterByName(String name, Pageable pageable);

}
