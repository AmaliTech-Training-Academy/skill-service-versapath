package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.SkillCapsuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CapsuleRepository extends JpaRepository<SkillCapsuleEntity, UUID> {
    Optional<SkillCapsuleEntity> findByName(String name);
}
