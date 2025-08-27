package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.capsule.CapsuleInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleSummaryResponseDto;
import com.capstone.skill_service.dto.track.TrackInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.track.TrackRequestDto;
import com.capstone.skill_service.dto.track.TrackResponseDto;
import com.capstone.skill_service.dto.track.TrackWithCapsuleResponseDto;
import com.capstone.skill_service.model.GrowthTrackEntity;
import com.capstone.skill_service.model.RouteTrackMappingEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-27T11:41:28+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class TrackMapperImpl implements TrackMapper {

    @Override
    public TrackResponseDto toDto(GrowthTrackEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TrackResponseDto.TrackResponseDtoBuilder trackResponseDto = TrackResponseDto.builder();

        trackResponseDto.capsules( trackCapsuleMappingEntityListToCapsuleSummaryResponseDtoList( entity.getSkillCapsules() ) );
        trackResponseDto.id( entity.getId() );
        trackResponseDto.name( entity.getName() );
        trackResponseDto.description( entity.getDescription() );
        trackResponseDto.estimatedMonths( entity.getEstimatedMonths() );
        trackResponseDto.status( entity.getStatus() );
        trackResponseDto.createdAt( entity.getCreatedAt() );
        trackResponseDto.updatedAt( entity.getUpdatedAt() );

        return trackResponseDto.build();
    }

    @Override
    public GrowthTrackEntity toEntity(TrackRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        GrowthTrackEntity.GrowthTrackEntityBuilder growthTrackEntity = GrowthTrackEntity.builder();

        growthTrackEntity.name( dto.getName() );
        growthTrackEntity.description( dto.getDescription() );
        growthTrackEntity.status( dto.getStatus() );
        growthTrackEntity.estimatedMonths( dto.getEstimatedMonths() );
        growthTrackEntity.createdAt( dto.getCreatedAt() );
        growthTrackEntity.updatedAt( dto.getUpdatedAt() );

        return growthTrackEntity.build();
    }

    @Override
    public TrackWithCapsuleResponseDto toWithCapsuleDto(GrowthTrackEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TrackWithCapsuleResponseDto.TrackWithCapsuleResponseDtoBuilder trackWithCapsuleResponseDto = TrackWithCapsuleResponseDto.builder();

        trackWithCapsuleResponseDto.id( entity.getId() );
        trackWithCapsuleResponseDto.name( entity.getName() );
        trackWithCapsuleResponseDto.description( entity.getDescription() );
        trackWithCapsuleResponseDto.estimatedMonths( entity.getEstimatedMonths() );
        trackWithCapsuleResponseDto.status( entity.getStatus() );
        trackWithCapsuleResponseDto.createdAt( entity.getCreatedAt() );

        return trackWithCapsuleResponseDto.build();
    }

    @Override
    public CapsuleSummaryResponseDto mapToCapsuleDto(TrackCapsuleMappingEntity mapping) {
        if ( mapping == null ) {
            return null;
        }

        CapsuleSummaryResponseDto.CapsuleSummaryResponseDtoBuilder capsuleSummaryResponseDto = CapsuleSummaryResponseDto.builder();

        capsuleSummaryResponseDto.id( mappingCapsuleId( mapping ) );
        capsuleSummaryResponseDto.name( mappingCapsuleName( mapping ) );
        capsuleSummaryResponseDto.difficulty( mappingCapsuleDifficulty( mapping ) );
        capsuleSummaryResponseDto.proficiencyLevel( mappingCapsuleProficiencyLevel( mapping ) );

        return capsuleSummaryResponseDto.build();
    }

    @Override
    public TrackInSequenceOrderResponseDto toInSequenceOrderDto(RouteTrackMappingEntity mapping) {
        if ( mapping == null ) {
            return null;
        }

        TrackInSequenceOrderResponseDto.TrackInSequenceOrderResponseDtoBuilder trackInSequenceOrderResponseDto = TrackInSequenceOrderResponseDto.builder();

        trackInSequenceOrderResponseDto.id( mappingGrowthTrackId( mapping ) );
        trackInSequenceOrderResponseDto.name( mappingGrowthTrackName( mapping ) );
        trackInSequenceOrderResponseDto.estimatedMonths( mappingGrowthTrackEstimatedMonths( mapping ) );
        trackInSequenceOrderResponseDto.status( mappingGrowthTrackStatus( mapping ) );
        trackInSequenceOrderResponseDto.description( mappingGrowthTrackDescription( mapping ) );
        List<TrackCapsuleMappingEntity> skillCapsules = mappingGrowthTrackSkillCapsules( mapping );
        trackInSequenceOrderResponseDto.capsules( trackCapsuleMappingEntityListToCapsuleInSequenceOrderResponseDtoList( skillCapsules ) );
        if ( mapping.getSequenceOrder() != null ) {
            trackInSequenceOrderResponseDto.sequenceOrder( mapping.getSequenceOrder() );
        }

        return trackInSequenceOrderResponseDto.build();
    }

    protected List<CapsuleSummaryResponseDto> trackCapsuleMappingEntityListToCapsuleSummaryResponseDtoList(List<TrackCapsuleMappingEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CapsuleSummaryResponseDto> list1 = new ArrayList<CapsuleSummaryResponseDto>( list.size() );
        for ( TrackCapsuleMappingEntity trackCapsuleMappingEntity : list ) {
            list1.add( mapToCapsuleDto( trackCapsuleMappingEntity ) );
        }

        return list1;
    }

    private UUID mappingCapsuleId(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return null;
        }
        UUID id = capsule.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String mappingCapsuleName(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return null;
        }
        String name = capsule.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String mappingCapsuleDifficulty(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return null;
        }
        String difficulty = capsule.getDifficulty();
        if ( difficulty == null ) {
            return null;
        }
        return difficulty;
    }

    private ProficiencyLevel mappingCapsuleProficiencyLevel(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return null;
        }
        ProficiencyLevel proficiencyLevel = capsule.getProficiencyLevel();
        if ( proficiencyLevel == null ) {
            return null;
        }
        return proficiencyLevel;
    }

    private UUID mappingGrowthTrackId(RouteTrackMappingEntity routeTrackMappingEntity) {
        if ( routeTrackMappingEntity == null ) {
            return null;
        }
        GrowthTrackEntity growthTrack = routeTrackMappingEntity.getGrowthTrack();
        if ( growthTrack == null ) {
            return null;
        }
        UUID id = growthTrack.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String mappingGrowthTrackName(RouteTrackMappingEntity routeTrackMappingEntity) {
        if ( routeTrackMappingEntity == null ) {
            return null;
        }
        GrowthTrackEntity growthTrack = routeTrackMappingEntity.getGrowthTrack();
        if ( growthTrack == null ) {
            return null;
        }
        String name = growthTrack.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private int mappingGrowthTrackEstimatedMonths(RouteTrackMappingEntity routeTrackMappingEntity) {
        if ( routeTrackMappingEntity == null ) {
            return 0;
        }
        GrowthTrackEntity growthTrack = routeTrackMappingEntity.getGrowthTrack();
        if ( growthTrack == null ) {
            return 0;
        }
        int estimatedMonths = growthTrack.getEstimatedMonths();
        return estimatedMonths;
    }

    private Status mappingGrowthTrackStatus(RouteTrackMappingEntity routeTrackMappingEntity) {
        if ( routeTrackMappingEntity == null ) {
            return null;
        }
        GrowthTrackEntity growthTrack = routeTrackMappingEntity.getGrowthTrack();
        if ( growthTrack == null ) {
            return null;
        }
        Status status = growthTrack.getStatus();
        if ( status == null ) {
            return null;
        }
        return status;
    }

    private String mappingGrowthTrackDescription(RouteTrackMappingEntity routeTrackMappingEntity) {
        if ( routeTrackMappingEntity == null ) {
            return null;
        }
        GrowthTrackEntity growthTrack = routeTrackMappingEntity.getGrowthTrack();
        if ( growthTrack == null ) {
            return null;
        }
        String description = growthTrack.getDescription();
        if ( description == null ) {
            return null;
        }
        return description;
    }

    private List<TrackCapsuleMappingEntity> mappingGrowthTrackSkillCapsules(RouteTrackMappingEntity routeTrackMappingEntity) {
        if ( routeTrackMappingEntity == null ) {
            return null;
        }
        GrowthTrackEntity growthTrack = routeTrackMappingEntity.getGrowthTrack();
        if ( growthTrack == null ) {
            return null;
        }
        List<TrackCapsuleMappingEntity> skillCapsules = growthTrack.getSkillCapsules();
        if ( skillCapsules == null ) {
            return null;
        }
        return skillCapsules;
    }

    protected CapsuleInSequenceOrderResponseDto trackCapsuleMappingEntityToCapsuleInSequenceOrderResponseDto(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }

        CapsuleInSequenceOrderResponseDto.CapsuleInSequenceOrderResponseDtoBuilder capsuleInSequenceOrderResponseDto = CapsuleInSequenceOrderResponseDto.builder();

        capsuleInSequenceOrderResponseDto.id( trackCapsuleMappingEntity.getId() );
        if ( trackCapsuleMappingEntity.getSequenceOrder() != null ) {
            capsuleInSequenceOrderResponseDto.sequenceOrder( trackCapsuleMappingEntity.getSequenceOrder() );
        }

        return capsuleInSequenceOrderResponseDto.build();
    }

    protected List<CapsuleInSequenceOrderResponseDto> trackCapsuleMappingEntityListToCapsuleInSequenceOrderResponseDtoList(List<TrackCapsuleMappingEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<CapsuleInSequenceOrderResponseDto> list1 = new ArrayList<CapsuleInSequenceOrderResponseDto>( list.size() );
        for ( TrackCapsuleMappingEntity trackCapsuleMappingEntity : list ) {
            list1.add( trackCapsuleMappingEntityToCapsuleInSequenceOrderResponseDto( trackCapsuleMappingEntity ) );
        }

        return list1;
    }
}
