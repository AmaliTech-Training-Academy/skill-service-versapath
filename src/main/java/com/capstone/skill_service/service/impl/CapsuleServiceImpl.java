package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.capsule.CapsuleRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleUpdateRequestDto;
import com.capstone.skill_service.exception.AtomNotFoundException;
import com.capstone.skill_service.exception.CapsuleExistsException;
import com.capstone.skill_service.exception.CapsuleNotFoundException;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillAtomEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.repository.AtomRepository;
import com.capstone.skill_service.repository.CapsuleRepository;
import com.capstone.skill_service.service.AtomService;
import com.capstone.skill_service.service.CapsuleService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CapsuleServiceImpl implements CapsuleService {
    private static final Logger logger = LoggerFactory.getLogger(CapsuleServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final AtomRepository atomRepository;

    @Override
    public CapsuleResponseDto create(CapsuleRequestDto dto) {
        if(findByName(dto.getName()).isPresent()){
            throw new CapsuleExistsException(
                    String.format("A Skill Capsule with the name '%s' already exist",
                            dto.getName()));
        }
        SkillCapsuleEntity capsuleEntity = this.capsuleMapper.toEntity(dto);

        capsuleEntity.setCreatedAt(LocalDateTime.now());
        capsuleEntity.setUpdatedAt(LocalDateTime.now());
        capsuleEntity.setStatus(Status.ACTIVE);

        // add skill atom to skill capsule collection
        List<CapsuleAtomMappingEntity> assignAtomToCapsuleCollection = new ArrayList<>();
        for(int i=0; i<dto.getAtomIds().size(); i++){
            UUID atomId=dto.getAtomIds().get(i); //get atom id

            SkillAtomEntity atom = this.atomRepository.findById(atomId)
                    .orElseThrow( () -> new AtomNotFoundException("A Skill atom provided doesn't exist")
                    );

            // assign atom to capsule
            CapsuleAtomMappingEntity assignAtomToCapsule = CapsuleAtomMappingEntity.builder()
                    .atom(atom)
                    .capsule(capsuleEntity)
                    .sequenceOrder(i+1) // set default sequence order
                    .build();

            assignAtomToCapsuleCollection.add(assignAtomToCapsule); // add a single assignment to assigment collection
        }
        capsuleEntity.setSkillAtoms(assignAtomToCapsuleCollection); // link capsule to all the atoms assigned to

        logger.info("Admin created skill capsule: {}", capsuleEntity.getName());

        return this.capsuleMapper.toDto(capsuleRepository.save(capsuleEntity));
    }

    @Override
    public Optional<SkillCapsuleEntity> findByName(String name) {
        return this.capsuleRepository.findByName(name);
    }

    @Override
    public CustomPageResponse<CapsuleResponseDto> findAll(Pageable pageable) {
        Page<SkillCapsuleEntity> capsuleList = this.capsuleRepository.findAllWithSkillAtoms(pageable);
        Page<CapsuleResponseDto> capsules = capsuleList.map(this.capsuleMapper::toDto);

        logger.info("Capsules list is fetched");

        return CustomPageResponse.<CapsuleResponseDto>builder()
                .items(capsules.getContent())
                .page(capsules.getNumber())
                .size(capsules.getSize())
                .totalElements(capsules.getTotalElements())
                .totalPages(capsules.getTotalPages())
                .hasNext(capsules.hasNext())
                .hasPrevious(capsules.hasPrevious())
                .build();
    }

    @Override
    public Optional<SkillCapsuleEntity> findById(UUID id) {
        return this.capsuleRepository.findByIdWithSkillAtoms(id);
    }

    @Override
    public CapsuleResponseDto getCapsule(UUID id) {
        SkillCapsuleEntity capsule = findById(id)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        logger.info("Skill capsule {} retrieved", capsule.getName());

        return this.capsuleMapper.toDto(capsule);
    }

    @Override
    public void deleteById(UUID id) {
        //TODO
    }

    @Override
    public CapsuleResponseDto partialUpdate(CapsuleUpdateRequestDto dto, UUID id) {
        //TODO
        return null;
    }

    @Override
    public CapsuleResponseDto updateStatus(Status status, UUID id) {
        //TODO
        return null;
    }

}
