package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterUpdateRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterWithCapsuleResponseDto;
import com.capstone.skill_service.model.ClusterEntity;
import com.capstone.skill_service.util.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface ClusterService {
    ClusterResponseDto create(ClusterRequestDto dto, MultipartFile image);
    Optional<ClusterEntity> findByName(String name);
    CustomPageResponse<ClusterResponseDto> findAll(Pageable pageable);
    Optional<ClusterEntity> findById(UUID id);
    ClusterResponseDto getCluster(UUID id);
    void deleteById(UUID id);
    ClusterResponseDto partialUpdate(ClusterUpdateRequestDto dto, UUID id);
    ClusterResponseDto updateStatus(Status status, UUID id);
    ClusterWithCapsuleResponseDto getClusterWithCapsules(UUID clusterId);
}
