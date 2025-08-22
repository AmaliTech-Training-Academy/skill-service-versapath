package com.capstone.skill_service.dto.atom;

import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtomResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String objectives;
    private int estimatedHours;
    private Status status;
    private int moodle_section_id;
    private LocalDateTime createdAt;
}
