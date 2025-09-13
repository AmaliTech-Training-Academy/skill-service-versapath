package com.capstone.skill_service.dto.cluster;

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
public class ClusterUpdateRequestDto {
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;
    private UUID clusterId;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "type cannot contain numbers")
    private String type;

    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
