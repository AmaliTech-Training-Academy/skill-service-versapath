package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomIdsRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleRequestDto;
import com.capstone.skill_service.dto.capsule.CapsuleResponseDto;
import com.capstone.skill_service.dto.capsule.CapsuleUpdateRequestDto;
import com.capstone.skill_service.exception.AlreadyAssignedException;
import com.capstone.skill_service.exception.AtomNotFoundException;
import com.capstone.skill_service.exception.CapsuleExistsException;
import com.capstone.skill_service.exception.CapsuleNotFoundException;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillAtomEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.repository.AtomRepository;
import com.capstone.skill_service.repository.CapsuleRepository;
import com.capstone.skill_service.service.CapsuleService;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        addAtomsToCapsule(capsuleEntity, dto.getAtomIds()); // link capsule to all the atoms assigned to

        logger.info("Admin created skill capsule: {}", capsuleEntity.getName());

        return this.capsuleMapper.toDto(capsuleRepository.save(capsuleEntity));
    }

    @Override
    public Optional<SkillCapsuleEntity> findByName(String name) {
        return this.capsuleRepository.findByName(name);
    }

    @Override
    @Cacheable("capsuleList")
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
    @CacheEvict(value = "capsuleList", key = "#id")
    public void deleteById(UUID id) {
        SkillCapsuleEntity capsule = findById(id)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        logger.info("Skill capsule {} deleted", capsule.getName());

        this.capsuleRepository.deleteById(id);
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "capsuleList", allEntries = true), // to update the entire list
            put = @CachePut(value = "capsule", key = "#result.id") // Different cache name for individual capsule
    )
    public CapsuleResponseDto partialUpdate(CapsuleUpdateRequestDto dto, UUID id) {
        SkillCapsuleEntity capsule = findById(id)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );
        if(dto.getName() != null){
            if(findByName(dto.getName()).isPresent()){
                throw new CapsuleExistsException(
                        String.format("A Skill Capsule with the name '%s' already exist",
                                dto.getName()));
            }
            capsule.setName(dto.getName());
        }
        if(dto.getObjectives() != null){
            capsule.setObjectives(dto.getObjectives());
        }
        if(dto.getDescription() != null){
            capsule.setDescription(dto.getDescription());
        }
        if(dto.getDifficulty() != null){
            capsule.setDifficulty(dto.getDifficulty());
        }
        if(dto.getProficiencyLevel() != null){
            capsule.setProficiencyLevel(dto.getProficiencyLevel());
        }
        if(dto.getCategoryType() != null){
            capsule.setCategoryType(dto.getCategoryType());
        }
        if(dto.getEstimatedHours() != capsule.getEstimatedHours()){
            capsule.setEstimatedHours(dto.getEstimatedHours());
        }
        if(dto.getStatus() != null){
            capsule.setStatus(dto.getStatus());
        }

        capsule.setUpdatedAt(LocalDateTime.now());

        logger.info("Skill capsule {} updated", capsule.getName());

        return this.capsuleMapper.toDto(this.capsuleRepository.save(capsule));
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "capsuleList", allEntries = true), // to update the entire list
            put = @CachePut(value = "capsule", key = "#result.id") // Different cache name for individual capsule
    )
    public CapsuleResponseDto updateStatus(Status status, UUID id) {
        SkillCapsuleEntity capsule = findById(id)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );
        capsule.setStatus(status);

        return this.capsuleMapper.toDto(this.capsuleRepository.save(capsule));
    }


    // update capsule by adding new atoms to it.
    @Override
    public CapsuleResponseDto assignAtomToCapsule(UUID capsuleId, AtomIdsRequestDto dto){
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        addAtomsToCapsule(capsuleEntity, dto.getAtomIds()); // link capsule to all the atoms assigned to

        logger.info("Admin added new skill atoms to skill capsule: {}", capsuleEntity.getName());

        return this.capsuleMapper.toDto(capsuleRepository.save(capsuleEntity));
    }

    void addAtomsToCapsule(SkillCapsuleEntity capsuleEntity, List<UUID> atomIds){

        int numberOfAtomsInCapsule = capsuleEntity.getSkillAtoms().size();

        for(int i=0; i<atomIds.size(); i++){
            UUID atomId=atomIds.get(i); //get atom id

            SkillAtomEntity atom = this.atomRepository.findById(atomId)
                    .orElseThrow( () -> new AtomNotFoundException("A Skill atom provided doesn't exist")
                    );

            boolean alreadyAssignedToCapsule = capsuleEntity.getSkillAtoms().stream()
                    .anyMatch(mapping -> mapping.getAtom().getId().equals(atomId));

            if(alreadyAssignedToCapsule){
                throw new AlreadyAssignedException("Atom is already assigned to this capsule");
            }

            // assign atom to capsule
            CapsuleAtomMappingEntity assignAtomToCapsule = CapsuleAtomMappingEntity.builder()
                    .atom(atom)
                    .capsule(capsuleEntity)
                    .sequenceOrder(numberOfAtomsInCapsule + i + 1) // set default sequence order, when child size is 0, order starts from 1 else starts from size number + 1
                    .build();

            capsuleEntity.getSkillAtoms().add(assignAtomToCapsule);// add a single assignment to assigment collection
        }

    }

    @Override
    @Transactional
    public CapsuleResponseDto removeAtomFromCapsule(UUID capsuleId, UUID atomId) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist"));

        // find the atom to remove from capsule
        CapsuleAtomMappingEntity atomToRemove = capsuleEntity.getSkillAtoms().stream()
                .filter(mapping -> mapping.getAtom().getId().equals(atomId))
                .findFirst()
                .orElseThrow(() -> new AtomNotFoundException("A skill atom provided doesn't exist in capsule"));

        capsuleEntity.getSkillAtoms().remove(atomToRemove); // remove atom from capsule collection

        //Rearrange the sequence order
        int order = 1;
        for (CapsuleAtomMappingEntity mapping : capsuleEntity.getSkillAtoms()) {
            mapping.setSequenceOrder(order++);
        }
        return capsuleMapper.toDto(capsuleRepository.save(capsuleEntity));
    }

}
