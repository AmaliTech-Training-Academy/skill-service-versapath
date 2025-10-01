package com.capstone.skill_service.dto.track;

import com.capstone.skill_service.util.Status;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TrackOnlyResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String description;
    private int estimatedMonths;
    private Status status;
    private String image;
    private long capsuleNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public TrackOnlyResponseDto(UUID id, String name, String description, int estimatedMonths, Status status,
                                String image, long capsuleNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.estimatedMonths = estimatedMonths;
        this.status = status;
        this.image = image;
        this.capsuleNumber = capsuleNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
