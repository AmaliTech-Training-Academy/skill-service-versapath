package com.capstone.skill_service.dto.atom;

import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtomUpdateRequestDto {
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    @Pattern(regexp = "^[0-9]+$", message = "only digits are allowed")
    private int estimatedHours;

    private String description;
    private String objectives;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
