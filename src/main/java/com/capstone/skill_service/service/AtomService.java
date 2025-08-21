package com.capstone.skill_service.service;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomRequestDto;
import com.capstone.skill_service.dto.atom.AtomResponseDto;
import com.capstone.skill_service.dto.atom.AtomUpdateRequestDto;
import com.capstone.skill_service.model.SkillAtomEntity;
import com.capstone.skill_service.util.Status;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AtomService {
    AtomResponseDto create(AtomRequestDto dto);
    Optional<SkillAtomEntity> findByName(String name);
    CustomPageResponse<AtomResponseDto> findAll(Pageable pageable);
    Optional<SkillAtomEntity> findById(UUID userId);
    AtomResponseDto getAtom(UUID userId);
    void deleteById(UUID id);
    AtomResponseDto partialUpdate(AtomUpdateRequestDto dto, UUID id);
    AtomResponseDto updateStatus(Status status, UUID id);
}
