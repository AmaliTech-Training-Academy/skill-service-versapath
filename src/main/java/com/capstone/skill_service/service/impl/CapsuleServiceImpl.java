package com.capstone.skill_service.service.impl;

import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.PaginationData;
import com.capstone.skill_service.dto.atom.AtomIdsRequestDto;
import com.capstone.skill_service.dto.atom.AtomInSequenceOrderResponseDto;
import com.capstone.skill_service.dto.capsule.*;
import com.capstone.skill_service.dto.cluster.ClusterIdsRequestDto;
import com.capstone.skill_service.dto.tag.TagIdsRequestDto;
import com.capstone.skill_service.exception.*;
import com.capstone.skill_service.mapper.AtomMapper;
import com.capstone.skill_service.mapper.CapsuleMapper;
import com.capstone.skill_service.messaging.CreateSkillProducerEvent;
import com.capstone.skill_service.messaging.PopulateSkillEvents;
import com.capstone.skill_service.model.*;
import com.capstone.skill_service.repository.*;
import com.capstone.skill_service.service.AwsFileUploadService;
import com.capstone.skill_service.service.CapsuleService;
import com.capstone.skill_service.service.PreSignedUrlService;
import com.capstone.skill_service.util.FileHelper;
import com.capstone.skill_service.util.Status;
import lombok.RequiredArgsConstructor;
import org.common.event.CreateSkillEvent;
import org.common.event.SkillAtom;
import org.common.event.SkillCapsuleEvent;
import org.common.event.UpdateSkillEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CapsuleServiceImpl implements CapsuleService {
    private static final Logger logger = LoggerFactory.getLogger(CapsuleServiceImpl.class);
    private final CapsuleRepository capsuleRepository;
    private final CapsuleMapper capsuleMapper;
    private final AtomMapper atomMapper;
    private final AtomRepository atomRepository;
    private final TagRepository tagRepository;
    private final ClusterRepository clusterRepository;
    private final TrackCapsuleMappingRepository trackCapsuleMappingRepository;
    private final CreateSkillProducerEvent createSkillProducerEvent;
    private final PopulateSkillEvents populateSkillEvents;
    private final AwsFileUploadService awsFileUploadService;
    private final PreSignedUrlService preSignedUrlService;

    @Override
    @CacheEvict(value = "capsuleList",  allEntries = true)
    public CapsuleResponseDto create(CapsuleRequestDto dto, MultipartFile image) throws IOException {
        if(findByName(dto.getName()).isPresent()){
            throw new CapsuleExistsException(
                    String.format("A Skill Capsule with the name '%s' already exist",
                            dto.getName()));
        }
        SkillCapsuleEntity capsuleEntity = this.capsuleMapper.toEntity(dto);

        capsuleEntity.setCreatedAt(LocalDateTime.now());
        capsuleEntity.setUpdatedAt(LocalDateTime.now());
        if(dto.getStatus() == null){ // set default value
            capsuleEntity.setStatus(Status.ACTIVE);
        }
        if(dto.getAtomIds() != null){
            addAtomsToCapsule(capsuleEntity, dto.getAtomIds()); // link capsule to all the atoms assigned to
        }else{
            addAtomsToCapsule(capsuleEntity, List.of());
        }
        if(dto.getTagIds() != null){
            addTagsToCapsule(capsuleEntity, dto.getTagIds()); // link capsule to all the tags assigned to
        }
        if(dto.getClusterIds() != null){
            addClustersToCapsule(capsuleEntity, dto.getClusterIds()); // link capsule to all the clusters assigned to
        }
        if(image != null){
            FileHelper.validateImage(image); // validate image
            capsuleEntity.setImage(awsFileUploadService.uploadFile(image)); //upload image
        }

        logger.info("Admin created skill capsule: {}", capsuleEntity.getName());
        SkillCapsuleEntity savedCapsule = capsuleRepository.save(capsuleEntity);

        // send an event command
        sendEventToCreateSkillStructureOnMoodle(savedCapsule);
        populateSkillCapsuleEvent(savedCapsule);

        // map capsule to response dto
        return mapCapsuleToResponseDtoWithAtom(savedCapsule);
    }

    void populateSkillCapsuleEvent(SkillCapsuleEntity capsuleEntity){

        List<Map<UUID, Integer>> listOfAtomsInCapsule = new ArrayList<>();

        // extract atom ID and sequence order
        for(CapsuleAtomMappingEntity mapping: capsuleEntity.getSkillAtoms()){
            Map<UUID, Integer> atomWithSequenceOrder = new HashMap<>();
            atomWithSequenceOrder.put(mapping.getAtom().getId(), mapping.getSequenceOrder());

            listOfAtomsInCapsule.add(atomWithSequenceOrder);

        }

        populateSkillEvents.populateSkillCapsule(SkillCapsuleEvent.builder()
                        .id(capsuleEntity.getId())
                        .name(capsuleEntity.getName())
                        .description(capsuleEntity.getDescription())
                        .difficulty(capsuleEntity.getDifficulty())
                        .proficiencyLevel(capsuleEntity.getProficiencyLevel().toString())
                        .skillAtom(listOfAtomsInCapsule)
                        .build());

    }

    void sendEventToCreateSkillStructureOnMoodle(SkillCapsuleEntity savedCapsule){
        // extract capsule atom names to send to Moodle
        List<SkillAtomEntity> atomEntities = savedCapsule.getSkillAtoms().stream()
                .map(CapsuleAtomMappingEntity::getAtom)
                .toList();

        List<String> atomNames = new ArrayList<>();
        for(SkillAtomEntity atom: atomEntities){
            atomNames.add(atom.getName());
        }

        CreateSkillEvent createSkillEvent = CreateSkillEvent.builder()
                .capsuleName(savedCapsule.getName())
                .atoms(atomNames)
                .build();

        createSkillProducerEvent.sendCreateSkillOnMoodleCommand(createSkillEvent);
    }

    public CapsuleResponseDto mapCapsuleToResponseDtoWithAtom(SkillCapsuleEntity capsule){
         //build atoms from mappings
        List<AtomInSequenceOrderResponseDto> atomInSequenceOrderResponseDto = capsule.getSkillAtoms().stream()
                .map(atomMapper::toInSequenceOrderDto)
                .toList();

        // map capsule to response dto
        CapsuleResponseDto response = capsuleMapper.toDto(capsule);

        response.setSkillAtoms(atomInSequenceOrderResponseDto); // update atomSequence in response

        FileHelper.generatePresignedUrl(capsule, response, preSignedUrlService);
        return response;
    }

    @Override
    public Optional<SkillCapsuleEntity> findByName(String name) {
        return this.capsuleRepository.findByName(name);
    }

    @Override
    @Cacheable("capsuleList")
    public CustomPageResponse<CapsuleWithAtomCountResponseDto> findAll(Pageable pageable) {
        Page<CapsuleWithAtomCountResponseDto> capsules = this.capsuleRepository.findCapsuleWithAtomCount(pageable);
        for(CapsuleWithAtomCountResponseDto capsule: capsules){
            // generate presigned url
            String imageUrl = FileHelper.getGeneratedPresignedUrl(this.capsuleMapper.toEntity(capsule),
                    preSignedUrlService);
            capsule.setImage(imageUrl);
        }
        logger.info("Capsules list is fetched");

        return CustomPageResponse.<CapsuleWithAtomCountResponseDto>builder()
                .items(capsules.getContent())
                .pagination(PaginationData.builder()
                    .page(capsules.getNumber())
                    .size(capsules.getSize())
                    .totalElements(capsules.getTotalElements())
                    .totalPages(capsules.getTotalPages())
                    .hasNext(capsules.hasNext())
                    .hasPrevious(capsules.hasPrevious())
                    .build())
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
    @CacheEvict(value = "capsuleList", allEntries = true)
    public void deleteById(UUID id) {
        SkillCapsuleEntity capsule = findById(id)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );
        // first delete all the reference
        List<TrackCapsuleMappingEntity> mappings = trackCapsuleMappingRepository.findBySkillCapsuleId(id);
        trackCapsuleMappingRepository.deleteAll(mappings);

        logger.info("Skill capsule {} deleted", capsule.getName());

        this.capsuleRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "capsuleWithDetails", key = "#capsuleId")
    public CapsuleWithDetailsResponseDto getCapsuleWithDetails(UUID capsuleId) {
        SkillCapsuleEntity capsule = capsuleRepository.findByIdWithDetails(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("Capsule not found"));

        // generate presigned url
        String imageUrl = FileHelper.getGeneratedPresignedUrl(capsule,
                preSignedUrlService);
        capsule.setImage(imageUrl);

        return capsuleMapper.toWithDetailsDto(capsule);
    }

    @Override
    public void updateSkillWithMoodleData(UpdateSkillEvent updateSkillEvent) {
        if(capsuleRepository.findByName(updateSkillEvent.getName()).isPresent()){
            SkillCapsuleEntity capsuleEntity = capsuleRepository.findByName(updateSkillEvent.getName())
                    .orElseThrow(() -> new CapsuleNotFoundException("Capsule not found"));

            for(SkillAtom atom: updateSkillEvent.getSkillAtoms()){
                SkillAtomEntity atomEntity = atomRepository.findByName(atom.getName())
                        .orElseThrow(() -> new AtomNotFoundException("Skill atom not found"));

                // map atom to Moodle module id and page id
                atomEntity.setMoodleModuleId(atom.getCourseModuleId());
                atomEntity.setMoodlePageId(atom.getPageId());

                atomRepository.save(atomEntity); //save to DB
            }

            capsuleEntity.setMoodleCourseId(updateSkillEvent.getCourseId()); // map capsule to Moodle course id
            capsuleRepository.save(capsuleEntity); // save to DB


            logger.info("Capsule with its atom are updated with Moodle data {}", updateSkillEvent.getName());

        }
    }

    @Override
    public CustomPageResponse<CapsuleWithAtomCountResponseDto> filterCapsules(String name, Pageable pageable) {
        Page<CapsuleWithAtomCountResponseDto> capsules;

        if(name == null || name.trim().isEmpty()){
             capsules = this.capsuleRepository.findByNameContainingIgnoreCase(null, PageRequest.of(0, 20));
        }else{
            capsules = this.capsuleRepository.findByNameContainingIgnoreCase(name, pageable);
        }

        for(CapsuleWithAtomCountResponseDto capsule: capsules){
            // generate presigned url
            String imageUrl = FileHelper.getGeneratedPresignedUrl(this.capsuleMapper.toEntity(capsule),
                    preSignedUrlService);
            capsule.setImage(imageUrl);
        }
        logger.info("Capsules list is filtered");

        return CustomPageResponse.<CapsuleWithAtomCountResponseDto>builder()
                .items(capsules.getContent())
                .pagination(PaginationData.builder()
                        .page(capsules.getNumber())
                        .size(capsules.getSize())
                        .totalElements(capsules.getTotalElements())
                        .totalPages(capsules.getTotalPages())
                        .hasNext(capsules.hasNext())
                        .hasPrevious(capsules.hasPrevious())
                        .build())
                .build();
    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#dto.getCapsuleId()")  // evict single capsule details
        }
    )
    public CapsuleResponseDto partialUpdate(CapsuleUpdateRequestDto dto, MultipartFile image) throws IOException {
        SkillCapsuleEntity capsule = findById(dto.getCapsuleId())
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
            if(dto.getEstimatedHours() < 0 || dto.getEstimatedHours() > 1000){
                throw new InvalidTimeException("Estimated hours is invalid, must be between 1 and 100");
            }
            capsule.setEstimatedHours(dto.getEstimatedHours());
        }
        if(dto.getStatus() != null){
            capsule.setStatus(dto.getStatus());
        }
        if(image != null){
            FileHelper.validateImage(image); // validate image
            capsule.setImage(awsFileUploadService.uploadFile(image)); //upload image
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
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#id")  // evict single capsule details
        }
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
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
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
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
    )
    public void removeAtomFromCapsule(UUID capsuleId, AtomIdsRequestDto atomIds) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist"));

        List<CapsuleAtomMappingEntity> atomsToRemove = new ArrayList<>(); //atoms to remove from capsule
        List<UUID> notFoundIds = new ArrayList<>(); // atoms not found

        for (UUID atomId : atomIds.getAtomIds()) {
            capsuleEntity.getSkillAtoms().stream()
                    .filter(mapping -> mapping.getAtom().getId().equals(atomId))
                    .findFirst()
                    .ifPresentOrElse(
                            atomsToRemove::add,
                            () -> notFoundIds.add(atomId)
                    );
        }

        // Remove atoms that exist
        capsuleEntity.getSkillAtoms().removeAll(atomsToRemove);

        //Rearrange the sequence order
        int order = 1;
        for (CapsuleAtomMappingEntity mapping : capsuleEntity.getSkillAtoms()) {
            mapping.setSequenceOrder(order++);
        }

        capsuleRepository.save(capsuleEntity);

        // Throw exception for not found atoms
        if (!notFoundIds.isEmpty()) {
            if(notFoundIds.size() == 1){
                throw new AtomNotFoundException("There is an atom that doesn't belong to this Capsule");
            }else{
                throw new AtomNotFoundException("There are some atoms do not belong to this Capsule ");
            }
        }

    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
    )
    public CapsuleResponseDto reorderAtoms(UUID capsuleId, List<UUID> orderedAtomIds) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow( () -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist")
                );

        // check whether all the incoming atoms exist in capsule list in DB
        List<CapsuleAtomMappingEntity> existingAtoms = capsuleEntity.getSkillAtoms(); // in database
        List<UUID> existingAtomIds = existingAtoms.stream().map(m->m.getAtom().getId()).toList();

        Set<UUID> noneDuplicateOrderedAtomIds = new HashSet<>(orderedAtomIds);

        if(! new HashSet<>(existingAtomIds).containsAll(orderedAtomIds) || noneDuplicateOrderedAtomIds.size() != existingAtoms.size()){
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

    void addTagsToCapsule(SkillCapsuleEntity capsuleEntity, Set<UUID> tagIds){
        if (capsuleEntity.getTags() == null) {
            capsuleEntity.setTags(new HashSet<>());
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

    void addClustersToCapsule(SkillCapsuleEntity capsuleEntity, Set<UUID> clusterIds){
        if (capsuleEntity.getClusters() == null) {
            capsuleEntity.setClusters(new HashSet<>());
        }

        for (UUID clusterId : clusterIds) {
            ClusterEntity cluster = this.clusterRepository.findById(clusterId)
                    .orElseThrow(() -> new ClusterNotFoundException("A cluster provided doesn't exist")
                    );

            boolean alreadyAssignedToCapsule = capsuleEntity.getClusters().stream()
                    .anyMatch(clusterItem -> clusterItem.getId().equals(clusterId));

            if (alreadyAssignedToCapsule) {
                throw new AlreadyAssignedException("The cluster is already assigned to this capsule");
            }

            capsuleEntity.getClusters().add(cluster);// add a cluster to capsule
        }

    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
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
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
    )
    public void removeTagFromCapsule(UUID capsuleId, UUID tagId) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist"));

        // find the tag to remove from capsule
        TagEntity tagToRemove = capsuleEntity.getTags().stream()
                .filter(mapping -> mapping.getId().equals(tagId))
                .findFirst()
                .orElseThrow(() -> new TagNotFoundException("A tag provided doesn't exist in capsule"));

        capsuleEntity.getTags().remove(tagToRemove); // remove tag from capsule

        capsuleRepository.save(capsuleEntity);

    }

    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
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
    @Caching(
        evict = {
            @CacheEvict(value = "capsuleList", allEntries = true), // refresh list
            @CacheEvict(value = "capsuleWithDetails", key = "#capsuleId")  // evict single capsule details
        }
    )
    public void removeClusterFromCapsule(UUID capsuleId, UUID clusterId) {
        SkillCapsuleEntity capsuleEntity = findById(capsuleId)
                .orElseThrow(() -> new CapsuleNotFoundException("A Skill capsule provided doesn't exist"));

        // find the cluster to remove from capsule
        ClusterEntity clusterToRemove = capsuleEntity.getClusters().stream()
                .filter(mapping -> mapping.getId().equals(clusterId))
                .findFirst()
                .orElseThrow(() -> new ClusterNotFoundException("A cluster provided doesn't exist in capsule"));

        capsuleEntity.getClusters().remove(clusterToRemove); // remove cluster from capsule

        capsuleRepository.save(capsuleEntity);

    }

}