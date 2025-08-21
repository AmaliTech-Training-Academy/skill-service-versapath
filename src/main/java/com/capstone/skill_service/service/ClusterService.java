package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterUpdateRequestDto;
import com.capstone.skill_service.model.ClusterEntity;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ClusterService {
    ClusterResponseDto create(ClusterRequestDto dto);
    Optional<ClusterEntity> findByName(String name);
    CustomPageResponse<ClusterResponseDto> findAll(Pageable pageable);
    Optional<ClusterEntity> findById(UUID userId);
    ClusterResponseDto getCluster(UUID userId);
    void deleteById(UUID id);
    ClusterResponseDto partialUpdate(ClusterUpdateRequestDto dto, UUID id);
}
