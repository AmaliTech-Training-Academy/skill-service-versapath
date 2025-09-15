package com.capstone.skill_service.dto.capsule;

import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.util.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapsuleInSequenceOrderResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private int estimatedHours;
    private int moodleCourseId;
    private String difficulty;
    private ProficiencyLevel proficiencyLevel;
    private String description;
    List<AtomInSequenceOrderResponseDto> skillAtoms; // list of skill atom in sequence order
    private int sequenceOrder;
}
