package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleOnlyResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleSummaryWithNoClusterResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleWithDetailsResponseDto;
import com.capstone.skill_service.dto.cluster.ClusterSummaryResponseDto;
import com.capstone.skill_service.dto.tag.TagSummaryResponseDto;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.ClusterEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.model.TagEntity;
import com.capstone.skill_service.model.TrackCapsuleMappingEntity;
import com.capstone.skill_service.util.ProficiencyLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-27T09:53:28+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class CapsuleMapperImpl implements CapsuleMapper {

    @Override
    public CapsuleResponseDto toDto(SkillCapsuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CapsuleResponseDto.CapsuleResponseDtoBuilder capsuleResponseDto = CapsuleResponseDto.builder();

        capsuleResponseDto.id( entity.getId() );
        capsuleResponseDto.name( entity.getName() );
        capsuleResponseDto.estimatedHours( entity.getEstimatedHours() );
        capsuleResponseDto.difficulty( entity.getDifficulty() );
        capsuleResponseDto.proficiencyLevel( entity.getProficiencyLevel() );
        capsuleResponseDto.categoryType( entity.getCategoryType() );
        capsuleResponseDto.description( entity.getDescription() );
        capsuleResponseDto.objectives( entity.getObjectives() );
        capsuleResponseDto.status( entity.getStatus() );
        capsuleResponseDto.skillAtoms( capsuleAtomMappingEntityListToAtomInSequenceOrderResponseDtoList( entity.getSkillAtoms() ) );
        capsuleResponseDto.tags( tagEntityListToTagSummaryResponseDtoList( entity.getTags() ) );
        capsuleResponseDto.clusters( clusterEntityListToClusterSummaryResponseDtoList( entity.getClusters() ) );
        capsuleResponseDto.createdAt( entity.getCreatedAt() );

        return capsuleResponseDto.build();
    }

    @Override
    public CapsuleWithDetailsResponseDto toWithDetailsDto(SkillCapsuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CapsuleWithDetailsResponseDto.CapsuleWithDetailsResponseDtoBuilder capsuleWithDetailsResponseDto = CapsuleWithDetailsResponseDto.builder();

        capsuleWithDetailsResponseDto.id( entity.getId() );
        capsuleWithDetailsResponseDto.name( entity.getName() );
        capsuleWithDetailsResponseDto.estimatedHours( entity.getEstimatedHours() );
        capsuleWithDetailsResponseDto.difficulty( entity.getDifficulty() );
        capsuleWithDetailsResponseDto.proficiencyLevel( entity.getProficiencyLevel() );
        capsuleWithDetailsResponseDto.categoryType( entity.getCategoryType() );
        capsuleWithDetailsResponseDto.description( entity.getDescription() );
        capsuleWithDetailsResponseDto.objectives( entity.getObjectives() );
        capsuleWithDetailsResponseDto.status( entity.getStatus() );
        capsuleWithDetailsResponseDto.createdAt( entity.getCreatedAt() );

        return capsuleWithDetailsResponseDto.build();
    }

    @Override
    public CapsuleOnlyResponseDto toCapsuleOnlyDto(SkillCapsuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CapsuleOnlyResponseDto.CapsuleOnlyResponseDtoBuilder capsuleOnlyResponseDto = CapsuleOnlyResponseDto.builder();

        capsuleOnlyResponseDto.id( entity.getId() );
        capsuleOnlyResponseDto.name( entity.getName() );
        capsuleOnlyResponseDto.estimatedHours( entity.getEstimatedHours() );
        capsuleOnlyResponseDto.difficulty( entity.getDifficulty() );
        capsuleOnlyResponseDto.proficiencyLevel( entity.getProficiencyLevel() );
        capsuleOnlyResponseDto.categoryType( entity.getCategoryType() );
        capsuleOnlyResponseDto.description( entity.getDescription() );
        capsuleOnlyResponseDto.objectives( entity.getObjectives() );
        capsuleOnlyResponseDto.status( entity.getStatus() );
        capsuleOnlyResponseDto.createdAt( entity.getCreatedAt() );

        return capsuleOnlyResponseDto.build();
    }

    @Override
    public SkillCapsuleEntity toEntity(CapsuleRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        SkillCapsuleEntity.SkillCapsuleEntityBuilder skillCapsuleEntity = SkillCapsuleEntity.builder();

        skillCapsuleEntity.name( dto.getName() );
        skillCapsuleEntity.description( dto.getDescription() );
        skillCapsuleEntity.objectives( dto.getObjectives() );
        skillCapsuleEntity.categoryType( dto.getCategoryType() );
        skillCapsuleEntity.difficulty( dto.getDifficulty() );
        skillCapsuleEntity.proficiencyLevel( dto.getProficiencyLevel() );
        skillCapsuleEntity.status( dto.getStatus() );
        skillCapsuleEntity.estimatedHours( dto.getEstimatedHours() );
        skillCapsuleEntity.createdAt( dto.getCreatedAt() );
        skillCapsuleEntity.updatedAt( dto.getUpdatedAt() );

        return skillCapsuleEntity.build();
    }

    @Override
    public CapsuleSummaryWithNoClusterResponseDto toNoClusterDto(SkillCapsuleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CapsuleSummaryWithNoClusterResponseDto.CapsuleSummaryWithNoClusterResponseDtoBuilder capsuleSummaryWithNoClusterResponseDto = CapsuleSummaryWithNoClusterResponseDto.builder();

        capsuleSummaryWithNoClusterResponseDto.skillAtoms( capsuleAtomMappingEntityListToAtomInSequenceOrderResponseDtoList1( entity.getSkillAtoms() ) );
        capsuleSummaryWithNoClusterResponseDto.tags( tagEntityListToTagSummaryResponseDtoList1( entity.getTags() ) );
        capsuleSummaryWithNoClusterResponseDto.id( entity.getId() );
        capsuleSummaryWithNoClusterResponseDto.name( entity.getName() );
        capsuleSummaryWithNoClusterResponseDto.estimatedHours( entity.getEstimatedHours() );
        capsuleSummaryWithNoClusterResponseDto.difficulty( entity.getDifficulty() );
        capsuleSummaryWithNoClusterResponseDto.proficiencyLevel( entity.getProficiencyLevel() );
        capsuleSummaryWithNoClusterResponseDto.description( entity.getDescription() );

        return capsuleSummaryWithNoClusterResponseDto.build();
    }

    @Override
    public CapsuleInSequenceOrderResponseDto toInSequenceOrderDto(TrackCapsuleMappingEntity mapping) {
        if ( mapping == null ) {
            return null;
        }

        CapsuleInSequenceOrderResponseDto.CapsuleInSequenceOrderResponseDtoBuilder capsuleInSequenceOrderResponseDto = CapsuleInSequenceOrderResponseDto.builder();

        capsuleInSequenceOrderResponseDto.id( mappingCapsuleId( mapping ) );
        capsuleInSequenceOrderResponseDto.name( mappingCapsuleName( mapping ) );
        capsuleInSequenceOrderResponseDto.estimatedHours( mappingCapsuleEstimatedHours( mapping ) );
        capsuleInSequenceOrderResponseDto.difficulty( mappingCapsuleDifficulty( mapping ) );
        capsuleInSequenceOrderResponseDto.proficiencyLevel( mappingCapsuleProficiencyLevel( mapping ) );
        capsuleInSequenceOrderResponseDto.description( mappingCapsuleDescription( mapping ) );
        List<CapsuleAtomMappingEntity> skillAtoms = mappingCapsuleSkillAtoms( mapping );
        capsuleInSequenceOrderResponseDto.skillAtoms( capsuleAtomMappingEntityListToAtomInSequenceOrderResponseDtoList1( skillAtoms ) );
        if ( mapping.getSequenceOrder() != null ) {
            capsuleInSequenceOrderResponseDto.sequenceOrder( mapping.getSequenceOrder() );
        }

        return capsuleInSequenceOrderResponseDto.build();
    }

    protected AtomInSequenceOrderResponseDto capsuleAtomMappingEntityToAtomInSequenceOrderResponseDto(CapsuleAtomMappingEntity capsuleAtomMappingEntity) {
        if ( capsuleAtomMappingEntity == null ) {
            return null;
        }

        AtomInSequenceOrderResponseDto.AtomInSequenceOrderResponseDtoBuilder atomInSequenceOrderResponseDto = AtomInSequenceOrderResponseDto.builder();

        atomInSequenceOrderResponseDto.id( capsuleAtomMappingEntity.getId() );
        if ( capsuleAtomMappingEntity.getSequenceOrder() != null ) {
            atomInSequenceOrderResponseDto.sequenceOrder( capsuleAtomMappingEntity.getSequenceOrder() );
        }

        return atomInSequenceOrderResponseDto.build();
    }

    protected List<AtomInSequenceOrderResponseDto> capsuleAtomMappingEntityListToAtomInSequenceOrderResponseDtoList(List<CapsuleAtomMappingEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<AtomInSequenceOrderResponseDto> list1 = new ArrayList<AtomInSequenceOrderResponseDto>( list.size() );
        for ( CapsuleAtomMappingEntity capsuleAtomMappingEntity : list ) {
            list1.add( capsuleAtomMappingEntityToAtomInSequenceOrderResponseDto( capsuleAtomMappingEntity ) );
        }

        return list1;
    }

    protected TagSummaryResponseDto tagEntityToTagSummaryResponseDto(TagEntity tagEntity) {
        if ( tagEntity == null ) {
            return null;
        }

        TagSummaryResponseDto.TagSummaryResponseDtoBuilder tagSummaryResponseDto = TagSummaryResponseDto.builder();

        tagSummaryResponseDto.id( tagEntity.getId() );
        tagSummaryResponseDto.name( tagEntity.getName() );

        return tagSummaryResponseDto.build();
    }

    protected List<TagSummaryResponseDto> tagEntityListToTagSummaryResponseDtoList(List<TagEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<TagSummaryResponseDto> list1 = new ArrayList<TagSummaryResponseDto>( list.size() );
        for ( TagEntity tagEntity : list ) {
            list1.add( tagEntityToTagSummaryResponseDto( tagEntity ) );
        }

        return list1;
    }

    protected ClusterSummaryResponseDto clusterEntityToClusterSummaryResponseDto(ClusterEntity clusterEntity) {
        if ( clusterEntity == null ) {
            return null;
        }

        ClusterSummaryResponseDto.ClusterSummaryResponseDtoBuilder clusterSummaryResponseDto = ClusterSummaryResponseDto.builder();

        clusterSummaryResponseDto.id( clusterEntity.getId() );
        clusterSummaryResponseDto.name( clusterEntity.getName() );

        return clusterSummaryResponseDto.build();
    }

    protected List<ClusterSummaryResponseDto> clusterEntityListToClusterSummaryResponseDtoList(List<ClusterEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ClusterSummaryResponseDto> list1 = new ArrayList<ClusterSummaryResponseDto>( list.size() );
        for ( ClusterEntity clusterEntity : list ) {
            list1.add( clusterEntityToClusterSummaryResponseDto( clusterEntity ) );
        }

        return list1;
    }

    protected List<AtomInSequenceOrderResponseDto> capsuleAtomMappingEntityListToAtomInSequenceOrderResponseDtoList1(List<CapsuleAtomMappingEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<AtomInSequenceOrderResponseDto> list1 = new ArrayList<AtomInSequenceOrderResponseDto>( list.size() );
        for ( CapsuleAtomMappingEntity capsuleAtomMappingEntity : list ) {
            list1.add( capsuleAtomMappingEntityToAtomInSequenceOrderResponseDto( capsuleAtomMappingEntity ) );
        }

        return list1;
    }

    protected List<TagSummaryResponseDto> tagEntityListToTagSummaryResponseDtoList1(List<TagEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<TagSummaryResponseDto> list1 = new ArrayList<TagSummaryResponseDto>( list.size() );
        for ( TagEntity tagEntity : list ) {
            list1.add( tagEntityToTagSummaryResponseDto( tagEntity ) );
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

    private int mappingCapsuleEstimatedHours(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return 0;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return 0;
        }
        int estimatedHours = capsule.getEstimatedHours();
        return estimatedHours;
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

    private String mappingCapsuleDescription(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return null;
        }
        String description = capsule.getDescription();
        if ( description == null ) {
            return null;
        }
        return description;
    }

    private List<CapsuleAtomMappingEntity> mappingCapsuleSkillAtoms(TrackCapsuleMappingEntity trackCapsuleMappingEntity) {
        if ( trackCapsuleMappingEntity == null ) {
            return null;
        }
        SkillCapsuleEntity capsule = trackCapsuleMappingEntity.getCapsule();
        if ( capsule == null ) {
            return null;
        }
        List<CapsuleAtomMappingEntity> skillAtoms = capsule.getSkillAtoms();
        if ( skillAtoms == null ) {
            return null;
        }
        return skillAtoms;
    }
}
