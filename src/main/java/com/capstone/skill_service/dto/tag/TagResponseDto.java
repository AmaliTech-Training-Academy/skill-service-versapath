package com.capstone.skill_service.dto.tag;

import com.capstone.skill_service.util.Status;
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
public class TagResponseDto {
    private UUID id;
    private String name;
    private String type;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
}
