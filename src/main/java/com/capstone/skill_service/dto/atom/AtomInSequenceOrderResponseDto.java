package com.capstone.skill_service.dto.atom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtomInSequenceOrderResponseDto {
    private UUID id;
    private String name;
    private String description;
    private int moodle_section_id;
    private int sequenceOrder;
}
