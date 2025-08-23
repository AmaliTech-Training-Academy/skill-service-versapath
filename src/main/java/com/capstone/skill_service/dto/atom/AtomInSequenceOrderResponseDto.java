package com.capstone.skill_service.dto.atom;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class AtomInSequenceOrderResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String description;
    private int moodleSectionId;
    private int sequenceOrder;
}
