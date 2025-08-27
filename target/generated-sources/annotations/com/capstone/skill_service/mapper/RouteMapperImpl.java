package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.route.RouteRequestDto;
import com.capstone.skill_service.dto.route.RouteResponseDto;
import com.capstone.skill_service.dto.route.RouteWithTrackResponseDto;
import com.capstone.skill_service.dto.track.TrackSummaryResponseDto;
import com.capstone.skill_service.model.GrowthTrackEntity;
import com.capstone.skill_service.model.RouteTrackMappingEntity;
import com.capstone.skill_service.model.TalentRouteEntity;
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
public class RouteMapperImpl implements RouteMapper {

    @Override
    public RouteResponseDto toDto(TalentRouteEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RouteResponseDto.RouteResponseDtoBuilder routeResponseDto = RouteResponseDto.builder();

        routeResponseDto.tracks( routeTrackMappingEntityListToTrackSummaryResponseDtoList( entity.getTracks() ) );
        routeResponseDto.id( entity.getId() );
        routeResponseDto.name( entity.getName() );
        routeResponseDto.roleName( entity.getRoleName() );
        routeResponseDto.description( entity.getDescription() );
        routeResponseDto.status( entity.getStatus() );
        routeResponseDto.createdAt( entity.getCreatedAt() );
        routeResponseDto.updatedAt( entity.getUpdatedAt() );

        return routeResponseDto.build();
    }

    @Override
    public TalentRouteEntity toEntity(RouteRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        TalentRouteEntity.TalentRouteEntityBuilder talentRouteEntity = TalentRouteEntity.builder();

        talentRouteEntity.name( dto.getName() );
        talentRouteEntity.roleName( dto.getRoleName() );
        talentRouteEntity.description( dto.getDescription() );
        talentRouteEntity.status( dto.getStatus() );
        talentRouteEntity.createdAt( dto.getCreatedAt() );
        talentRouteEntity.updatedAt( dto.getUpdatedAt() );

        return talentRouteEntity.build();
    }

    @Override
    public RouteWithTrackResponseDto toWithTrackDto(TalentRouteEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RouteWithTrackResponseDto.RouteWithTrackResponseDtoBuilder routeWithTrackResponseDto = RouteWithTrackResponseDto.builder();

        routeWithTrackResponseDto.id( entity.getId() );
        routeWithTrackResponseDto.name( entity.getName() );
        routeWithTrackResponseDto.roleName( entity.getRoleName() );
        routeWithTrackResponseDto.description( entity.getDescription() );
        routeWithTrackResponseDto.status( entity.getStatus() );
        routeWithTrackResponseDto.createdAt( entity.getCreatedAt() );

        return routeWithTrackResponseDto.build();
    }

    @Override
    public TrackSummaryResponseDto mapToTrackDto(RouteTrackMappingEntity mapping) {
        if ( mapping == null ) {
            return null;
        }

        TrackSummaryResponseDto.TrackSummaryResponseDtoBuilder trackSummaryResponseDto = TrackSummaryResponseDto.builder();

        trackSummaryResponseDto.id( mappingGrowthTrackId( mapping ) );
        trackSummaryResponseDto.name( mappingGrowthTrackName( mapping ) );
        trackSummaryResponseDto.status( mappingGrowthTrackStatus( mapping ) );
        trackSummaryResponseDto.estimatedMonths( mappingGrowthTrackEstimatedMonths( mapping ) );

        return trackSummaryResponseDto.build();
    }

    protected List<TrackSummaryResponseDto> routeTrackMappingEntityListToTrackSummaryResponseDtoList(List<RouteTrackMappingEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<TrackSummaryResponseDto> list1 = new ArrayList<TrackSummaryResponseDto>( list.size() );
        for ( RouteTrackMappingEntity routeTrackMappingEntity : list ) {
            list1.add( mapToTrackDto( routeTrackMappingEntity ) );
        }

        return list1;
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
}
