package com.capstone.skill_service.dto.route;

import com.capstone.skill_service.dto.capsule.CapsuleSummaryResponseDto;
import com.capstone.skill_service.dto.track.TrackSummaryResponseDto;
import com.capstone.skill_service.util.Status;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class RouteResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String name;
    private String roleName;
    private String description;
    private Status status;
    private List<TrackSummaryResponseDto> tracks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
