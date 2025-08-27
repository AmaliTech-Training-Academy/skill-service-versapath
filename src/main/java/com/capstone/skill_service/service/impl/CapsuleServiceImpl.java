package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomIdsRequestDto;
import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.*;
import com.capstone.skill_service.dto.cluster.ClusterIdsRequestDto;
import com.capstone.skill_service.dto.tag.TagIdsRequestDto;
import com.capstone.skill_service.dto.tag.TagResponseDto;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.AtomMapper;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.mapper.ClusterMapper;
import com.capstone.skill_service.mapper.TagMapper;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapsuleServiceImpl implements CapsuleService {
    private static final Logger logger = LoggerFactory.getLogger(CapsuleServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final TagMapper tagMapper;
    private final ClusterMapper clusterMapper;
    private final AtomMapper atomMapper;
    private final AtomRepository atomRepository;
    private final TagRepository tagRepository;
    private final ClusterRepository clusterRepository;
    private final CapsuleAtomMappingRepository capsuleAtomMappingRepository;

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
        addTagsToCapsule(capsuleEntity, dto.getTagIds()); // link capsule to all the tags assigned to
        addClustersToCapsule(capsuleEntity, dto.getClusterIds()); // link capsule to all the clusters assigned to

        logger.info("Admin created skill capsule: {}", capsuleEntity.getName());
        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    public CapsuleResponseDto mapCapsuleToResponseDtoWithAtom(SkillCapsuleEntity capsule){
        // build atoms from mappings
        List<AtomInSequenceOrderResponseDto> atomInSequenceOrderResponseDto = capsule.getSkillAtoms().stream()
                .map(atomMapper::toInSequenceOrderDto)
                .toList();

        // map capsule to response dto
        CapsuleResponseDto response = capsuleMapper.toDto(capsule);

        response.setSkillAtoms(atomInSequenceOrderResponseDto); // update atomSequence in response

        return response;
    }

    @Override
    public Optional<SkillCapsuleEntity> findByName(String name) {
        return this.capsuleRepository.findByName(name);
    }

    @Override
    @Cacheable("capsuleList")
    public CustomPageResponse<CapsuleOnlyResponseDto> findAll(Pageable pageable) {
        Page<SkillCapsuleEntity> capsuleList = this.capsuleRepository.findAll(pageable);
        Page<CapsuleOnlyResponseDto> capsules = capsuleList.map(this.capsuleMapper::toCapsuleOnlyDto);

        logger.info("Tags list is fetched");

        return CustomPageResponse.<CapsuleOnlyResponseDto>builder()
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
    @Cacheable(value = "capsule", key = "#id")
    public CapsuleResponseDto getCapsule(UUID id) {
        SkillCapsuleEntity capsule = findById(id)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        logger.info("Skill capsule {} retrieved", capsule.getName());

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsule);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
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
    @Cacheable(value = "capsule", key = "#capsuleId")
    public CapsuleWithDetailsResponseDto getCapsuleWithDetails(UUID capsuleId) {
        // fetch capsule
        SkillCapsuleEntity capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("Capsule not found"));

        // fet capsule with children
        List<TagEntity> tags = tagRepository.findTagsByCapsuleId(capsuleId);
        List<ClusterEntity> clusters = clusterRepository.findClustersByCapsuleId(capsuleId);

        List<CapsuleAtomMappingEntity> mappings =
                capsuleAtomMappingRepository.findByCapsuleIdWithAtoms(capsuleId);

        // Group atoms and keep the sequence (declared in AtomInSequenceOrderResponseDto)
        List<AtomInSequenceOrderResponseDto> atoms = mappings.stream()
                .map(atomMapper::toInSequenceOrderDto) // map to dto expected in capsule response
                .toList();

        // Map capsule entity to response dto
        CapsuleWithDetailsResponseDto dto = capsuleMapper.toWithDetailsDto(capsule);

        // attach children
        dto.setSkillAtoms(atoms);
        dto.setClusters(clusters.stream()
                .map(clusterMapper::toSummaryDto) //map to dto expected in capsule response
                .toList());
        dto.setTags(tags.stream()
                .map(tagMapper::toSummaryDto) //map to dto expected in capsule response
                .toList());

        return dto;
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
        if(dto.getAtomIds() != null){
            addAtomsToCapsule(capsule, dto.getAtomIds());
        }
        if(dto.getTagIds() != null){
            addTagsToCapsule(capsule, dto.getTagIds());
        }
        if(dto.getClusterIds() != null){
            addClustersToCapsule(capsule, dto.getClusterIds());
        }

        capsule.setUpdatedAt(LocalDateTime.now());

        logger.info("Skill capsule {} updated", capsule.getName());

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsule);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
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

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsule);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }


    // update capsule by adding new atoms to it.
    @Override
    @Caching(
            evict = @CacheEvict(value = "capsuleList", allEntries = true), // to update the entire list
            put = @CachePut(value = "capsule", key = "#result.id") // Different cache name for individual capsule
    )
    public CapsuleResponseDto assignAtomToCapsule(UUID capsuleId, AtomIdsRequestDto dto){
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        addAtomsToCapsule(capsuleEntity, dto.getAtomIds()); // link capsule to all the atoms assigned to

        logger.info("Admin added new skill atoms to skill capsule: {}", capsuleEntity.getName());

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    void addAtomsToCapsule(SkillCapsuleEntity capsuleEntity, List<UUID> atomIds){
        if (capsuleEntity.getSkillAtoms() == null) {
            capsuleEntity.setSkillAtoms(new ArrayList<>());
        }

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
    @Caching(evict = @CacheEvict(value = "capsuleList", key = "#result.id"))
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
        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "capsuleList", allEntries = true), // to update the entire list
            put = @CachePut(value = "capsule", key = "#result.id") // Different cache name for individual capsule
    )
    public CapsuleResponseDto reorderAtoms(UUID capsuleId, List<UUID> orderedAtomIds) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        // check whether all the incoming atoms exist in capsule list in DB
        List<CapsuleAtomMappingEntity> existingAtoms = capsuleEntity.getSkillAtoms(); // in database
        List<UUID> existingAtomIds = existingAtoms.stream().map(m->m.getAtom().getId()).toList();

        Set<UUID> noneDuplicateOrderedAtomIds = new HashSet<>(orderedAtomIds);

        if(! new HashSet<>(existingAtomIds).containsAll(orderedAtomIds) || noneDuplicateOrderedAtomIds.size() != orderedAtomIds.size()){
            throw new InvalidAtomIdsException("Invalid skill atoms for this capsule");
        }

        // Update sequence order
        for (int i = 0; i < orderedAtomIds.size(); i++) {
            UUID atomId = orderedAtomIds.get(i); // incoming atom

            CapsuleAtomMappingEntity existingAtomMapping = existingAtoms.stream()
                    .filter(m -> m.getAtom().getId().equals(atomId))
                    .findFirst()
                    .orElseThrow(() -> new AtomNotFoundException("A skill atom provided doesn't exist in capsule"));

            existingAtomMapping.setSequenceOrder(i + 1); // update sequence order
        }
        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    void addTagsToCapsule(SkillCapsuleEntity capsuleEntity, List<UUID> tagIds){
        if (capsuleEntity.getTags() == null) {
            capsuleEntity.setTags(new ArrayList<>());
        }
        //get tag id
        for (UUID tagId : tagIds) {
            TagEntity tag = this.tagRepository.findById(tagId)
                    .orElseThrow(() -> new TagNotFoundException("A tag provided doesn't exist")
                    );

            boolean alreadyAssignedToCapsule = capsuleEntity.getTags().stream()
                    .anyMatch(tagItem -> tagItem.getId().equals(tagId));

            if (alreadyAssignedToCapsule) {
                throw new AlreadyAssignedException("The tag is already assigned to this capsule");
            }

            capsuleEntity.getTags().add(tag);// add a tag to capsule
        }

    }

    void addClustersToCapsule(SkillCapsuleEntity capsuleEntity, List<UUID> clusterIds){
        if (capsuleEntity.getClusters() == null) {
            capsuleEntity.setClusters(new ArrayList<>());
        }
        //get tag id
        for (UUID clusterId : clusterIds) {
            ClusterEntity cluster = this.clusterRepository.findById(clusterId)
                    .orElseThrow(() -> new ClusterNotFoundException("A cluster provided doesn't exist")
                    );

            boolean alreadyAssignedToCapsule = capsuleEntity.getClusters().stream()
                    .anyMatch(clusterItem -> clusterItem.getId().equals(clusterId));

            if (alreadyAssignedToCapsule) {
                throw new AlreadyAssignedException("The cluster is already assigned to this capsule");
            }

            capsuleEntity.getClusters().add(cluster);// add a tag to capsule
        }

    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "capsuleList", allEntries = true), // to update the entire list
            put = @CachePut(value = "capsule", key = "#result.id") // Different cache name for individual capsule
    )
    public CapsuleResponseDto assignTagToCapsule(UUID capsuleId, TagIdsRequestDto dto){
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        addTagsToCapsule(capsuleEntity, dto.getTagIds()); // link capsule to all the tags assigned to

        logger.info("Admin added new tags to skill capsule: {}", capsuleEntity.getName());

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    @Override
    @Transactional
    @Caching(evict = @CacheEvict(value = "capsuleList", key = "#result.id"))
    public CapsuleResponseDto removeTagFromCapsule(UUID capsuleId, UUID tagId) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist"));

        // find the tag to remove from capsule
        TagEntity tagToRemove = capsuleEntity.getTags().stream()
                .filter(mapping -> mapping.getId().equals(tagId))
                .findFirst()
                .orElseThrow(() -> new TagNotFoundException("A tag provided doesn't exist in capsule"));

        capsuleEntity.getTags().remove(tagToRemove); // remove tag from capsule

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    @Override
    @Caching(
            evict = @CacheEvict(value = "capsuleList", allEntries = true), // to update the entire list
            put = @CachePut(value = "capsule", key = "#result.id") // Different cache name for individual capsule
    )
    public CapsuleResponseDto assignClusterToCapsule(UUID capsuleId, ClusterIdsRequestDto dto){
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        addClustersToCapsule(capsuleEntity, dto.getClusterIds()); // link capsule to all the clusters assigned to

        logger.info("Admin added new clusters to skill capsule: {}", capsuleEntity.getName());

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    @Override
    @Transactional
    @Caching(evict = @CacheEvict(value = "capsuleList", key = "#result.id"))
    public CapsuleResponseDto removeClusterFromCapsule(UUID capsuleId, UUID clusterId) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist"));

        // find the cluster to remove from capsule
        ClusterEntity clusterToRemove = capsuleEntity.getClusters().stream()
                .filter(mapping -> mapping.getId().equals(clusterId))
                .findFirst()
                .orElseThrow(() -> new ClusterNotFoundException("A cluster provided doesn't exist in capsule"));

        capsuleEntity.getClusters().remove(clusterToRemove); // remove cluster from capsule

        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

}
