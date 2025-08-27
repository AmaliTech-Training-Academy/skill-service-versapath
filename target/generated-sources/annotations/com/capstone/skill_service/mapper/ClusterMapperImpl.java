package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.cluster.ClusterRequestDto;
import com.capstone.skill_service.dto.cluster.ClusterResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterSummaryResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterWithCapsuleResponseDto;
import com.capstone.skill_service.model.ClusterEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-27T11:41:28+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class ClusterMapperImpl implements ClusterMapper {

    @Override
    public ClusterResponseDto toDto(ClusterEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClusterResponseDto.ClusterResponseDtoBuilder clusterResponseDto = ClusterResponseDto.builder();

        clusterResponseDto.id( entity.getId() );
        clusterResponseDto.name( entity.getName() );
        clusterResponseDto.type( entity.getType() );
        clusterResponseDto.description( entity.getDescription() );
        clusterResponseDto.status( entity.getStatus() );
        clusterResponseDto.createdAt( entity.getCreatedAt() );

        return clusterResponseDto.build();
    }

    @Override
    public ClusterSummaryResponseDto toSummaryDto(ClusterEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClusterSummaryResponseDto.ClusterSummaryResponseDtoBuilder clusterSummaryResponseDto = ClusterSummaryResponseDto.builder();

        clusterSummaryResponseDto.id( entity.getId() );
        clusterSummaryResponseDto.name( entity.getName() );

        return clusterSummaryResponseDto.build();
    }

    @Override
    public ClusterEntity toEntity(ClusterRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        ClusterEntity.ClusterEntityBuilder clusterEntity = ClusterEntity.builder();

        clusterEntity.name( dto.getName() );
        clusterEntity.type( dto.getType() );
        clusterEntity.description( dto.getDescription() );
        clusterEntity.status( dto.getStatus() );
        clusterEntity.createdAt( dto.getCreatedAt() );
        clusterEntity.updatedAt( dto.getUpdatedAt() );

        return clusterEntity.build();
    }

    @Override
    public ClusterWithCapsuleResponseDto toWithCapsuleDto(ClusterEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClusterWithCapsuleResponseDto.ClusterWithCapsuleResponseDtoBuilder clusterWithCapsuleResponseDto = ClusterWithCapsuleResponseDto.builder();

        clusterWithCapsuleResponseDto.id( entity.getId() );
        clusterWithCapsuleResponseDto.name( entity.getName() );
        clusterWithCapsuleResponseDto.type( entity.getType() );
        clusterWithCapsuleResponseDto.description( entity.getDescription() );
        clusterWithCapsuleResponseDto.status( entity.getStatus() );
        clusterWithCapsuleResponseDto.createdAt( entity.getCreatedAt() );

        return clusterWithCapsuleResponseDto.build();
    }
}
