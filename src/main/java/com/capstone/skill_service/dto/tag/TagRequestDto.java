package com.capstone.skill_service.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequestDto {
    private String name;
    private String type;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
