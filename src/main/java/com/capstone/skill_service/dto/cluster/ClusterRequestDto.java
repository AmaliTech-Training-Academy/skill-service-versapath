package com.capstone.skill_service.dto.cluster;

import com.capstone.skill_service.util.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must have at least 3 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "name cannot contain numbers")
    private String name;

    @Pattern(regexp = "^[A-Za-z ]+$", message = "type cannot contain numbers")
    private String type;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Status status;

}
