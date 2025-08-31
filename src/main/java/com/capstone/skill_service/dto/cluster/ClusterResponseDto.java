package com.capstone.skill_service.dto.cluster;

import com.capstone.skill_service.util.Status;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ClusterResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String type;
    private String description;
    private String imageName;
    private Status status;
    long capsulesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClusterResponseDto(UUID id, String name, String type, String description,
                              String imageName, Status status, long capsulesCount,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.imageName = imageName;
        this.status = status;
        this.capsulesCount = capsulesCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
