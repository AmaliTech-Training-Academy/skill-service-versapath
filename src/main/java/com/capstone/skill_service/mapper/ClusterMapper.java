package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.model.ClusterEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClusterMapper {
    ClusterResponseDto toDto(ClusterEntity entity);
    ClusterEntity toEntity(ClusterRequestDto dto);

}
