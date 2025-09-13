package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterSummaryResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterWithCapsuleResponseDto;
import com.capstone.skill_service.model.ClusterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClusterMapper {
    ClusterResponseDto toDto(ClusterEntity entity);
    ClusterSummaryResponseDto toSummaryDto(ClusterEntity entity);
    ClusterEntity toEntity(ClusterRequestDto dto);
    ClusterEntity toEntity(ClusterResponseDto dto);
    @Mapping(target = "capsules",  ignore = true)
    ClusterWithCapsuleResponseDto toWithCapsuleDto(ClusterEntity entity);

}
