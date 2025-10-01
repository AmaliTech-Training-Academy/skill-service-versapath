package com.capstone.skill_service.dto.route;

import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class RouteUpdateRequestDto {
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    @Size(min = 3, message = "Role name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Role name cannot contain numbers")
    private String roleName;

    private UUID talentRouteId;

    List<UUID> growthTrackIds; // list of growth track ids
    private String description;
    private Status status;
    private LocalDateTime updatedAt;
}