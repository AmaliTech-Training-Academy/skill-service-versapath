package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.SkillCapsuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapsuleRepository extends JpaRepository<SkillCapsuleEntity, UUID> {
    Optional<SkillCapsuleEntity> findByName(String name);

    @Query("""
        SELECT c
        FROM SkillCapsuleEntity c
        LEFT JOIN FETCH c.skillAtoms sa
        LEFT JOIN FETCH sa.atom
        WHERE c.id = :id
    """)
    Optional<SkillCapsuleEntity> findByIdWithSkillAtoms(@Param("id") UUID id);

    @Query("""
    SELECT DISTINCT c
    FROM SkillCapsuleEntity c
    LEFT JOIN FETCH c.tags t
    LEFT JOIN FETCH c.skillAtoms sa
    LEFT JOIN FETCH sa.atom a
    WHERE c.id IN :capsuleIds
    ORDER BY sa.sequenceOrder
""")
    List<SkillCapsuleEntity> findCapsulesWithTagsAndAtoms(@Param("capsuleIds") List<UUID> capsuleIds);

    @Query("""
    SELECT DISTINCT c
    FROM SkillCapsuleEntity c
    LEFT JOIN FETCH c.tags t
    LEFT JOIN FETCH c.clusters cl
    LEFT JOIN FETCH c.skillAtoms sa
    LEFT JOIN FETCH sa.atom a
    WHERE c.id = :capsuleId
    ORDER BY sa.sequenceOrder
""")
    Optional<SkillCapsuleEntity> findByIdWithDetails(@Param("capsuleId") UUID capsuleId);

}
