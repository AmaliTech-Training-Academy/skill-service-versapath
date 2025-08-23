package com.capstone.skill_service.dto.atom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtomIdsRequestDto {
    private List<UUID> atomIds;
}
