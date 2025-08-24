package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleSummaryWithNoClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterUpdateRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterWithCapsuleResponseDto;
import com.capstone.skill_service.exception.ClusterExistsException;
import com.capstone.skill_service.exception.ClusterNotFoundException;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.ClusterMapper;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.ClusterEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.ClusterService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {
    private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
    private final ClusterRepository clusterRepository;
    private final CapsuleMapper capsuleMapper;
    private final TagMapper tagMapper;
    private final CapsuleAtomMappingRepository capsuleAtomMappingRepository;
    private final TagRepository tagRepository;
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
        clusterEntity.setStatus(Status.ACTIVE);
        logger.info("Admin created skill cluster: {}", clusterEntity.getName());

        return this.clusterMapper.toDto(clusterRepository.save(clusterEntity));
    }

    @Override
    public Optional<ClusterEntity> findByName(String name) {
        return this.clusterRepository.findByName(name);
    }

    @Override
    @Cacheable("clusterList")
    public CustomPageResponse<ClusterResponseDto> findAll(Pageable pageable) {
        Page<ClusterEntity> clusterList = this.clusterRepository.findAll(pageable);
        Page<ClusterResponseDto> clusters = clusterList.map(this.clusterMapper::toDto);

        logger.info("Clusters list is fetched");

        return CustomPageResponse.<ClusterResponseDto>builder()
                .items(clusters.getContent())
                .page(clusters.getNumber())
                .size(clusters.getSize())
                .totalElements(clusters.getTotalElements())
                .totalPages(clusters.getTotalPages())
                .hasNext(clusters.hasNext())
                .hasPrevious(clusters.hasPrevious())
                .build();
    }

    @Override
    public Optional<ClusterEntity> findById(UUID id) {
        return this.clusterRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        ClusterEntity cluster = findById(id)
                .orElseThrow( () -> new ClusterNotFoundException("A cluster provided doesn't exist")
                );

        logger.info("Skill cluster {} deleted", cluster.getName());

        this.clusterRepository.deleteById(id);
    }

    @Override
    public ClusterResponseDto getCluster(UUID id) {
        ClusterEntity cluster = findById(id)
                .orElseThrow( () -> new ClusterNotFoundException("A cluster provided doesn't exist")
                );

        logger.info("Skill cluster {} retrieved", cluster.getName());

        return this.clusterMapper.toDto(cluster);
    }

    @Override
    public ClusterResponseDto partialUpdate(ClusterUpdateRequestDto dto, UUID id) {
        ClusterEntity cluster = findById(id)
                .orElseThrow( () -> new ClusterNotFoundException("A cluster provided doesn't exist")
                );
        if(dto.getName() != null){
            if(findByName(dto.getName()).isPresent()){
                throw new ClusterExistsException(
                        String.format("A Cluster with the name '%s' already exist",
                                dto.getName()));
            }
            cluster.setName(dto.getName());
        }
        if(dto.getType() != null){
            cluster.setType(dto.getType());
        }
        if(dto.getDescription() != null){
            cluster.setDescription(dto.getDescription());
        }
        if(dto.getStatus() != null){
            cluster.setStatus(dto.getStatus());
        }
        cluster.setUpdatedAt(LocalDateTime.now());

        logger.info("Skill cluster {} updated", cluster.getName());

        return this.clusterMapper.toDto(this.clusterRepository.save(cluster));
    }

    @Override
    public ClusterResponseDto updateStatus(Status status, UUID id) {
        ClusterEntity cluster = findById(id)
                .orElseThrow( () -> new ClusterNotFoundException("A cluster provided doesn't exist")
                );
        cluster.setStatus(status);

        return this.clusterMapper.toDto(this.clusterRepository.save(cluster));
    }

    @Override
    public ClusterWithCapsuleResponseDto getClusterWithCapsules(UUID clusterId) {
        ClusterEntity cluster = clusterRepository.findByIdWithCapsules(clusterId)
                .orElseThrow(() -> new ClusterNotFoundException("Cluster not found"));

        // capsules belong to the cluster
        List<SkillCapsuleEntity> capsules = cluster.getCapsules();

        if (capsules.isEmpty()) {
            ClusterWithCapsuleResponseDto dto = clusterMapper.toWithCapsuleDto(cluster);
            dto.setCapsules(List.of()); // return empty list in capsule field
            return dto;
        }

        // fetch children
        List<UUID> capsuleIds = capsules.stream().map(SkillCapsuleEntity::getId).toList();
        List<TagEntity> tagsByCapsule = tagRepository.findTagsByCapsuleIds(capsuleIds);
        List<CapsuleAtomMappingEntity> mappings = capsuleAtomMappingRepository.findByCapsuleIdsWithAtoms(capsuleIds);

        // group atoms by capsule
        Map<UUID, List<AtomInSequenceOrderResponseDto>> atomsByCapsule = mappings.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCapsule().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(capsuleMapper::toAtomDto, Collectors.toList())
                ));

        // map capsules to ResponseDto
        List<CapsuleSummaryWithNoClusterResponseDto> capsuleResponse = capsules.stream()
                .map(capsule -> {
                    CapsuleSummaryWithNoClusterResponseDto dto = capsuleMapper.toNoClusterDto(capsule);
                    //link atoms
                    dto.setSkillAtoms(atomsByCapsule.getOrDefault(capsule.getId(), List.of()));
                    //link tags
                    dto.setTags(tagsByCapsule.stream()
                            .map(tagMapper::toSummaryDto)
                            .toList());

                    return dto;
                })
                .toList();

        // build the final dto response
        ClusterWithCapsuleResponseDto dto = clusterMapper.toWithCapsuleDto(cluster);
        dto.setCapsules(capsuleResponse);

        return dto;
    }
}
