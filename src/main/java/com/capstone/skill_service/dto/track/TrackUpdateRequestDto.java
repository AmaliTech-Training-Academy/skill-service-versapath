package com.capstone.skill_service.dto.track;

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
public class TrackUpdateRequestDto {
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    private UUID trackId;
    private int estimatedMonths;
    List<UUID> capsuleIds; // list of capsule ids
    private String description;
    private Status status;
    private LocalDateTime updatedAt;
}
