package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.atom.AtomRequestDto;
import com.capstone.skill_service.dto.atom.AtomResponseDto;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillAtomEntity;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-27T09:53:29+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class AtomMapperImpl implements AtomMapper {

    @Override
    public AtomResponseDto toDto(SkillAtomEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AtomResponseDto.AtomResponseDtoBuilder atomResponseDto = AtomResponseDto.builder();

        atomResponseDto.id( entity.getId() );
        atomResponseDto.name( entity.getName() );
        atomResponseDto.description( entity.getDescription() );
        atomResponseDto.objectives( entity.getObjectives() );
        atomResponseDto.estimatedHours( entity.getEstimatedHours() );
        atomResponseDto.status( entity.getStatus() );
        atomResponseDto.moodleSectionId( entity.getMoodleSectionId() );
        atomResponseDto.createdAt( entity.getCreatedAt() );

        return atomResponseDto.build();
    }

    @Override
    public SkillAtomEntity toEntity(AtomRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        SkillAtomEntity.SkillAtomEntityBuilder skillAtomEntity = SkillAtomEntity.builder();

        skillAtomEntity.name( dto.getName() );
        skillAtomEntity.description( dto.getDescription() );
        skillAtomEntity.objectives( dto.getObjectives() );
        skillAtomEntity.status( dto.getStatus() );
        skillAtomEntity.estimatedHours( dto.getEstimatedHours() );
        skillAtomEntity.createdAt( dto.getCreatedAt() );
        skillAtomEntity.updatedAt( dto.getUpdatedAt() );

        return skillAtomEntity.build();
    }

    @Override
    public AtomInSequenceOrderResponseDto toInSequenceOrderDto(CapsuleAtomMappingEntity mapping) {
        if ( mapping == null ) {
            return null;
        }

        AtomInSequenceOrderResponseDto.AtomInSequenceOrderResponseDtoBuilder atomInSequenceOrderResponseDto = AtomInSequenceOrderResponseDto.builder();

        atomInSequenceOrderResponseDto.id( mappingAtomId( mapping ) );
        atomInSequenceOrderResponseDto.name( mappingAtomName( mapping ) );
        atomInSequenceOrderResponseDto.description( mappingAtomDescription( mapping ) );
        atomInSequenceOrderResponseDto.moodleSectionId( mappingAtomMoodleSectionId( mapping ) );
        if ( mapping.getSequenceOrder() != null ) {
            atomInSequenceOrderResponseDto.sequenceOrder( mapping.getSequenceOrder() );
        }

        return atomInSequenceOrderResponseDto.build();
    }

    private UUID mappingAtomId(CapsuleAtomMappingEntity capsuleAtomMappingEntity) {
        if ( capsuleAtomMappingEntity == null ) {
            return null;
        }
        SkillAtomEntity atom = capsuleAtomMappingEntity.getAtom();
        if ( atom == null ) {
            return null;
        }
        UUID id = atom.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String mappingAtomName(CapsuleAtomMappingEntity capsuleAtomMappingEntity) {
        if ( capsuleAtomMappingEntity == null ) {
            return null;
        }
        SkillAtomEntity atom = capsuleAtomMappingEntity.getAtom();
        if ( atom == null ) {
            return null;
        }
        String name = atom.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String mappingAtomDescription(CapsuleAtomMappingEntity capsuleAtomMappingEntity) {
        if ( capsuleAtomMappingEntity == null ) {
            return null;
        }
        SkillAtomEntity atom = capsuleAtomMappingEntity.getAtom();
        if ( atom == null ) {
            return null;
        }
        String description = atom.getDescription();
        if ( description == null ) {
            return null;
        }
        return description;
    }

    private int mappingAtomMoodleSectionId(CapsuleAtomMappingEntity capsuleAtomMappingEntity) {
        if ( capsuleAtomMappingEntity == null ) {
            return 0;
        }
        SkillAtomEntity atom = capsuleAtomMappingEntity.getAtom();
        if ( atom == null ) {
            return 0;
        }
        int moodleSectionId = atom.getMoodleSectionId();
        return moodleSectionId;
    }
}
