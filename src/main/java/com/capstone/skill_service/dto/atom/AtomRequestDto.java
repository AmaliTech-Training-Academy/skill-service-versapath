package com.capstone.skill_service.dto.atom;

import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtomRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Invalid name")
    private String name;

    @Min(value = 1, message = "Atom must be at least 1 hour")
    @Max(value = 1000, message = "Atom must not exceed 1000 hours")
    private int estimatedHours;

    private String description;
    private String objectives;
    private Status status;
    private String contentType;

}
