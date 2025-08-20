package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {
    Optional<TagEntity> findByName(String email);
}
