package com.capstone.skill_service.dto.route;

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
public class RouteRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    @NotBlank(message = "Role name is required")
    @Size(min = 3, message = "Role name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Role name cannot contain numbers")
    private String roleName;

    List<UUID> trackIds; // list of growth track ids
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
