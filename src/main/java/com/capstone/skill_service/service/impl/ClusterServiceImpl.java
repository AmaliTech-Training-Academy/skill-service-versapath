package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;

import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.exception.ClusterExistsException;
import com.capstone.skill_service.exception.ClusterNotFoundException;
import com.capstone.skill_service.mapper.ClusterMapper;
import com.capstone.skill_service.model.ClusterEntity;
import com.capstone.skill_service.repository.ClusterRepository;
import com.capstone.skill_service.service.ClusterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {
    private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
    private final ClusterRepository clusterRepository;
    private final ClusterMapper clusterMapper;

    @Override
    public ClusterResponseDto create(ClusterRequestDto dto) {
        if(findByName(dto.getName()).isPresent()){
            throw new ClusterExistsException(
                    String.format("A Cluster with the name '%s' already exist",
                            dto.getName()));
        }
        ClusterEntity clusterEntity = this.clusterMapper.toEntity(dto);

        clusterEntity.setCreatedAt(LocalDateTime.now());
        clusterEntity.setUpdatedAt(LocalDateTime.now());
        logger.info("Admin created skill cluster: {}", clusterEntity.getName());

        return this.clusterMapper.toDto(clusterRepository.save(clusterEntity));
    }

    @Override
    public Optional<ClusterEntity> findByName(String name) {
        return this.clusterRepository.findByName(name);
    }

    @Override
    public Optional<ClusterEntity> findById(UUID id) {
        return this.clusterRepository.findById(id);
    }

}
