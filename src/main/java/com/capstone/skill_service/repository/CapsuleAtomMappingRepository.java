package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillAtomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapsuleAtomMappingRepository extends JpaRepository<CapsuleAtomMappingEntity, UUID> {
    @Query("""
    SELECT cam
    FROM CapsuleAtomMappingEntity cam
    JOIN FETCH cam.atom
    WHERE cam.capsule.id IN :capsuleIds
    ORDER BY cam.sequenceOrder
""")
    List<CapsuleAtomMappingEntity> findByCapsuleIdsWithAtoms(@Param("capsuleIds") List<UUID> capsuleIds);
    @Query("""
    SELECT cam
    FROM CapsuleAtomMappingEntity cam
    JOIN FETCH cam.atom
    WHERE cam.capsule.id = :capsuleId
    ORDER BY cam.sequenceOrder
""")
    List<CapsuleAtomMappingEntity> findByCapsuleIdWithAtoms(@Param("capsuleId") UUID capsuleId);

}
