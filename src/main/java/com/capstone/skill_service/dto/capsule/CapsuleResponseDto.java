package com.capstone.skill_service.dto.capsule;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapsuleResponseDto {
    private UUID id;
    private String name;
    private int estimatedHours;
    private String difficulty;
    private ProficiencyLevel proficiencyLevel;
    private String categoryType;
    private String description;
    private String objectives;
    private Status status;
    List<AtomInSequenceOrderResponseDto> skillAtoms; // list of skill atom in sequence order
    private LocalDateTime createdAt;
}
