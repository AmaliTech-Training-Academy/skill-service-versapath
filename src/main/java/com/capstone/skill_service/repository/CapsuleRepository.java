package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.CapsuleAtomMappingEntity;
import com.capstone.skill_service.model.SkillCapsuleEntity;
import com.capstone.skill_service.model.TagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapsuleRepository extends JpaRepository<SkillCapsuleEntity, UUID> {
    Optional<SkillCapsuleEntity> findByName(String name);

    // fetch skill with atom using a single query to avoid N+1
    @Query("""
        SELECT DISTINCT c FROM SkillCapsuleEntity c
        LEFT JOIN FETCH c.skillAtoms sa
        LEFT JOIN FETCH sa.atom
        ORDER BY c.createdAt DESC
    """)
    Page<SkillCapsuleEntity> findAllWithSkillAtoms(Pageable pageable);

    @Query("""
        SELECT c
        FROM SkillCapsuleEntity c
        LEFT JOIN FETCH c.skillAtoms sa
        LEFT JOIN FETCH sa.atom
        WHERE c.id = :id
    """)
    Optional<SkillCapsuleEntity> findByIdWithSkillAtoms(@Param("id") UUID id);

    /* to fetch capsule with its atom, tag */

    // capsule with pagination
    @Query("SELECT c.id FROM SkillCapsuleEntity c ORDER BY c.createdAt DESC")
    Page<UUID> findPagedIds(Pageable pageable);

    //fetch capsule only
    @Query("SELECT c FROM SkillCapsuleEntity c WHERE c.id IN :ids")
    List<SkillCapsuleEntity> findByIdIn(@Param("ids") List<UUID> ids);

}
