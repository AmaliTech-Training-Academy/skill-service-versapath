package com.capstone.skill_service.dto.capsule;

import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class CapsuleWithAtomCountResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private int moodleCourseId;
    private String difficulty;
    private ProficiencyLevel proficiencyLevel;
    private String categoryType;
    private String description;
    private String objectives;
    private int estimatedHours;
    private Status status;
    private long atomNumber;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CapsuleWithAtomCountResponseDto(UUID id, String name, String difficulty,int moodleCourseId,ProficiencyLevel proficiencyLevel,
                                           String categoryType,String description, String objectives,
                                           int estimatedHours, Status status, long atomNumber, String image,
                                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.estimatedHours = estimatedHours;
        this.status = status;
        this.atomNumber = atomNumber;
        this.difficulty = difficulty;
        this.moodleCourseId = moodleCourseId;
        this.proficiencyLevel = proficiencyLevel;
        this.categoryType = categoryType;
        this.description = description;
        this.objectives = objectives;
        this.image = image;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
