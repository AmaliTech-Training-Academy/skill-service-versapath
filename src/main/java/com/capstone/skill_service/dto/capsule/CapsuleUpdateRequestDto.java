package com.capstone.skill_service.dto.capsule;

import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapsuleUpdateRequestDto {
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    private int estimatedHours;
    private String difficulty;
    private ProficiencyLevel proficiency_level;
    private String category_type;
    private String description;
    private String objectives;
    private Status status;
    private LocalDateTime updatedAt;

}
