package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;

import com.capstone.skill_service.dto.PaginationData;
import com.capstone.skill_service.dto.capsule.CapsuleSummaryWithNoClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterUpdateRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterWithCapsuleResponseDto;
import com.capstone.skill_service.exception.ClusterException;
import com.capstone.skill_service.exception.ClusterExistsException;
import com.capstone.skill_service.exception.ClusterNotFoundException;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.ClusterMapper;
import com.capstone.skill_service.model.ClusterEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.AwsFileUploadService;
import com.capstone.skill_service.service.ClusterService;
import com.capstone.skill_service.service.PreSignedUrlService;
import com.capstone.skill_service.util.FileHelper;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

@Service
@RequiredArgsConstructor
public class ClusterServiceImpl implements ClusterService {
    private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
    private final ClusterRepository clusterRepository;
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final ClusterMapper clusterMapper;
    private final AwsFileUploadService awsFileUploadService;
    private final PreSignedUrlService preSignedUrlService;

    @Override
    @CacheEvict(value = "clusterList",allEntries = true)
    public ClusterResponseDto create(ClusterRequestDto dto, MultipartFile image) throws IOException{
        if(findByName(dto.getName()).isPresent()){
            throw new ClusterExistsException(
                    String.format("A Cluster with the name '%s' already exist",
                            dto.getName()));
        }
        ClusterEntity clusterEntity = this.clusterMapper.toEntity(dto);

        clusterEntity.setCreatedAt(LocalDateTime.now());
        clusterEntity.setUpdatedAt(LocalDateTime.now());
        if(dto.getStatus() == null){ // set default value
            clusterEntity.setStatus(Status.ACTIVE);
        }


        if(image != null){
            FileHelper.validateImage(image); // validate image
            clusterEntity.setImageName(awsFileUploadService.uploadFile(image)); //upload image
        }
        ClusterEntity saved = clusterRepository.save(clusterEntity);

        ClusterResponseDto responseDto = this.clusterMapper.toDto(saved);

        // generate presigned url
        FileHelper.generatePresignedUrl(saved, responseDto, preSignedUrlService);

        logger.info("Admin created skill cluster: {}", clusterEntity.getName());

        return responseDto;
    }

    @Override
    public Optional<ClusterEntity> findByName(String name) {
        return this.clusterRepository.findByName(name);
    }

    @Override
    @Cacheable("clusterList")
    public CustomPageResponse<ClusterResponseDto> findAll(Pageable pageable) {
        Page<ClusterResponseDto> clusterList = this.clusterRepository.findClustersWithCapsuleCount(pageable);

        for(ClusterResponseDto cluster: clusterList){
            // generate presigned url
            String imageUrl = FileHelper.getGeneratedPresignedUrl(this.clusterMapper.toEntity(cluster),
                    preSignedUrlService);
            cluster.setImageName(imageUrl);
        }

        logger.info("Clusters list is fetched");

        return CustomPageResponse.<ClusterResponseDto>builder()
                .items(clusterList.getContent())
                .pagination(PaginationData.builder()
                    .page(clusterList.getNumber())
                    .size(clusterList.getSize())
                    .totalElements(clusterList.getTotalElements())
                    .totalPages(clusterList.getTotalPages())
                    .hasNext(clusterList.hasNext())
                    .hasPrevious(clusterList.hasPrevious())
                    .build())
                .build();
    }

    @Override
    public Optional<ClusterEntity> findById(UUID id) {
        return this.clusterRepository.findById(id);
    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "clusterList", allEntries = true), // to update the entire list
            @CacheEvict(value = "clusterWithCapsule", key = "#id") // evict single cluster
        }
    )
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
    @Caching(
        evict = {
            @CacheEvict(value = "clusterList", allEntries = true), // to update the entire list
            @CacheEvict(value = "clusterWithCapsule", key = "#dto.getClusterId()") // evict single cluster
        }
    )
    public ClusterResponseDto partialUpdate(ClusterUpdateRequestDto dto, MultipartFile image) throws IOException {
        ClusterEntity cluster = findById(dto.getClusterId())
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
        if(image != null){
            FileHelper.validateImage(image); // validate image
            cluster.setImageName(awsFileUploadService.uploadFile(image)); //update image
        }
        cluster.setUpdatedAt(LocalDateTime.now());

        ClusterEntity saved = this.clusterRepository.save(cluster);
        ClusterResponseDto responseDto = this.clusterMapper.toDto(saved);

        // generate presigned url
        FileHelper.generatePresignedUrl(saved, responseDto, preSignedUrlService);

        logger.info("Skill cluster {} updated", cluster.getName());

        return responseDto;
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "clusterList", allEntries = true), // to update the entire list
            put = @CachePut(value = "cluster", key = "#result.id") // evict single cluster
    )
    public ClusterResponseDto updateStatus(Status status, UUID id) {
        ClusterEntity cluster = findById(id)
                .orElseThrow( () -> new ClusterNotFoundException("A cluster provided doesn't exist")
                );
        cluster.setStatus(status);

        return this.clusterMapper.toDto(this.clusterRepository.save(cluster));
    }

    @Override
    @Cacheable(value = "clusterWithCapsule", key = "#clusterId")
    public ClusterWithCapsuleResponseDto getClusterWithCapsules(UUID clusterId) {
        ClusterEntity cluster = clusterRepository.findByIdWithCapsules(clusterId)
                .orElseThrow(() -> new ClusterNotFoundException("Cluster not found"));

        // generate presigned url
        String imageUrl = FileHelper.getGeneratedPresignedUrl(cluster, preSignedUrlService);
        cluster.setImageName(imageUrl);

        // capsules' ids belonging to the cluster
        List<UUID> capsuleIds = clusterRepository.findCapsuleIdsByClusterId(cluster.getId());

        if (capsuleIds.isEmpty()) {
            ClusterWithCapsuleResponseDto dto = clusterMapper.toWithCapsuleDto(cluster);
            dto.setCapsules(List.of()); // return empty list in capsule field
            return dto;
        }

        // fetch capsules with tags and atoms in single query
        List<SkillCapsuleEntity> capsules =
                capsuleRepository.findCapsulesWithTagsAndAtoms(capsuleIds);

        // map capsule entity to desired dto
        List<CapsuleSummaryWithNoClusterResponseDto> capsulesWithChildren =
                capsules.stream()
                        .map(capsuleMapper::toNoClusterDto)
                        .toList();

        // build final cluster DTO
        ClusterWithCapsuleResponseDto dto = clusterMapper.toWithCapsuleDto(cluster);
        dto.setCapsules(capsulesWithChildren);

        return dto;
    }

    @Override
    public CustomPageResponse<ClusterResponseDto> filterClusters(String name, Pageable pageable) {
        Page<ClusterResponseDto> clusterList = null;

        if(name == null || name.trim().isEmpty()){
            clusterList = this.clusterRepository.findClustersWithCapsuleCount(PageRequest.of(0, 20));
        }else{
            clusterList = this.clusterRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        for(ClusterResponseDto cluster: clusterList){
            // generate pre-signed url
            String imageUrl = FileHelper.getGeneratedPresignedUrl(this.clusterMapper.toEntity(cluster),
                    preSignedUrlService);
            cluster.setImageName(imageUrl);
        }

        logger.info("Clusters list is filtered");

        return CustomPageResponse.<ClusterResponseDto>builder()
                .items(clusterList.getContent())
                .pagination(PaginationData.builder()
                        .page(clusterList.getNumber())
                        .size(clusterList.getSize())
                        .totalElements(clusterList.getTotalElements())
                        .totalPages(clusterList.getTotalPages())
                        .hasNext(clusterList.hasNext())
                        .hasPrevious(clusterList.hasPrevious())
                        .build())
                .build();
    }
}
