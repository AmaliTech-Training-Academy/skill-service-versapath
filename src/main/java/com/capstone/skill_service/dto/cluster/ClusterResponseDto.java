package com.capstone.skill_service.dto.cluster;

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
public class ClusterResponseDto {
    private UUID id;
    private String name;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}
