package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.SkillAtomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtomRepository extends JpaRepository<SkillAtomEntity, UUID> {
    Optional<SkillAtomEntity> findByName(String name);
}
