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
public class CapsuleUpdateRequestDto {
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    private int estimatedHours;
    private String difficulty;
    private ProficiencyLevel proficiencyLevel;
    private String categoryType;
    private String description;
    private String objectives;
    private Status status;
    List<UUID> atomIds;
    List<UUID> tagIds;
    List<UUID> clusterIds;
    private LocalDateTime updatedAt;

}
