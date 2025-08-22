package com.capstone.skill_service.dto.capsule;

import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.*;
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
public class CapsuleRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    @Min(value = 1, message = "Must be at least 1 hour")
    @Max(value = 1000, message = "Must not exceed 1000 hours")
    private int estimatedHours;

    private String difficulty;
    private ProficiencyLevel proficiency_level;
    private String category_type;
    private String description;
    private String objectives;
    private Status status;
    List<UUID> atomIds; // list of skill atom ids in sequence order
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
