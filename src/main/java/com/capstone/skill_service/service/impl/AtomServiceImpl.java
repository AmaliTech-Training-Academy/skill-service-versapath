package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomRequestDto;
import com.capstone.skill_service.dto.atom.AtomResponseDto;
import com.capstone.skill_service.dto.atom.AtomUpdateRequestDto;
import com.capstone.skill_service.exception.AtomExistsException;
import com.capstone.skill_service.exception.AtomNotFoundException;
import com.capstone.skill_service.mapper.AtomMapper;
import com.capstone.skill_service.model.SkillAtomEntity;
import com.capstone.skill_service.repository.AtomRepository;
import com.capstone.skill_service.service.AtomService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtomServiceImpl implements AtomService {
    private static final Logger logger = LoggerFactory.getLogger(AtomServiceImpl.class);
    private final AtomRepository atomRepository;
    private final AtomMapper atomMapper;

    @Override
    public AtomResponseDto create(AtomRequestDto dto) {
        if(findByName(dto.getName()).isPresent()){
            throw new AtomExistsException(
                    String.format("A Skill Atom with the name '%s' already exist",
                            dto.getName()));
        }
        SkillAtomEntity atomEntity = this.atomMapper.toEntity(dto);

        atomEntity.setCreatedAt(LocalDateTime.now());
        atomEntity.setUpdatedAt(LocalDateTime.now());
        atomEntity.setStatus(Status.ACTIVE);
        logger.info("Admin created skill atom: {}", atomEntity.getName());

        return this.atomMapper.toDto(atomRepository.save(atomEntity));
    }

    @Override
    public Optional<SkillAtomEntity> findByName(String name) {
        return this.atomRepository.findByName(name);
    }

    @Override
    public CustomPageResponse<AtomResponseDto> findAll(Pageable pageable) {
        Page<SkillAtomEntity> atomList = this.atomRepository.findAll(pageable);
        Page<AtomResponseDto> atoms = atomList.map(this.atomMapper::toDto);

        logger.info("Atoms list is fetched");

        return CustomPageResponse.<AtomResponseDto>builder()
                .items(atoms.getContent())
                .page(atoms.getNumber())
                .size(atoms.getSize())
                .totalElements(atoms.getTotalElements())
                .totalPages(atoms.getTotalPages())
                .hasNext(atoms.hasNext())
                .hasPrevious(atoms.hasPrevious())
                .build();
    }

    @Override
    public Optional<SkillAtomEntity> findById(UUID id) {
        return this.atomRepository.findById(id);
    }

    @Override
    public AtomResponseDto getAtom(UUID id) {
        SkillAtomEntity atom = findById(id)
                .orElseThrow( () -> new AtomNotFoundException("A Skill atom provided doesn't exist")
                );

        logger.info("Skill atom {} retrieved", atom.getName());

        return this.atomMapper.toDto(atom);
    }

    @Override
    public void deleteById(UUID id) {
        SkillAtomEntity atom = findById(id)
                .orElseThrow( () -> new AtomNotFoundException("A Skill atom provided doesn't exist")
                );

        logger.info("Skill atom {} deleted", atom.getName());

        this.atomRepository.deleteById(id);
    }

    @Override
    public AtomResponseDto partialUpdate(AtomUpdateRequestDto dto, UUID id) {
        SkillAtomEntity atom = findById(id)
                .orElseThrow( () -> new AtomNotFoundException("A Skill atom provided doesn't exist")
                );
        if(dto.getName() != null){
            atom.setName(dto.getName());
        }
        if(dto.getObjectives() != null){
            atom.setObjectives(dto.getObjectives());
        }
        if(dto.getDescription() != null){
            atom.setDescription(dto.getDescription());
        }
        if(dto.getUpdatedAt() != null){
            atom.setUpdatedAt(LocalDateTime.now());
        }

        logger.info("Skill atom {} updated", atom.getName());

        return this.atomMapper.toDto(this.atomRepository.save(atom));
    }

    @Override
    public AtomResponseDto updateStatus(Status status, UUID id) {
        SkillAtomEntity atom = findById(id)
                .orElseThrow( () -> new AtomNotFoundException("A Skill atom provided doesn't exist")
                );
        atom.setStatus(status);

        return this.atomMapper.toDto(this.atomRepository.save(atom));
    }

}
