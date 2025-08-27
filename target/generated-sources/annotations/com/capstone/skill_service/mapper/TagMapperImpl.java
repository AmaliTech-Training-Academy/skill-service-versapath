package com.capstone.skill_service.mapper;

import com.capstone.skill_service.dto.tag.TagRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.dto.tag.TagSummaryResponseDto;
import com.capstone.skill_service.model.TagEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-27T09:53:29+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class TagMapperImpl implements TagMapper {

    @Override
    public TagResponseDto toDto(TagEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TagResponseDto.TagResponseDtoBuilder tagResponseDto = TagResponseDto.builder();

        tagResponseDto.id( entity.getId() );
        tagResponseDto.name( entity.getName() );
        tagResponseDto.type( entity.getType() );
        tagResponseDto.description( entity.getDescription() );
        tagResponseDto.status( entity.getStatus() );
        tagResponseDto.createdAt( entity.getCreatedAt() );

        return tagResponseDto.build();
    }

    @Override
    public TagSummaryResponseDto toSummaryDto(TagEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TagSummaryResponseDto.TagSummaryResponseDtoBuilder tagSummaryResponseDto = TagSummaryResponseDto.builder();

        tagSummaryResponseDto.id( entity.getId() );
        tagSummaryResponseDto.name( entity.getName() );

        return tagSummaryResponseDto.build();
    }

    @Override
    public TagEntity toEntity(TagRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        TagEntity.TagEntityBuilder tagEntity = TagEntity.builder();

        tagEntity.name( dto.getName() );
        tagEntity.type( dto.getType() );
        tagEntity.description( dto.getDescription() );
        tagEntity.status( dto.getStatus() );
        tagEntity.createdAt( dto.getCreatedAt() );
        tagEntity.updatedAt( dto.getUpdatedAt() );

        return tagEntity.build();
    }
}
