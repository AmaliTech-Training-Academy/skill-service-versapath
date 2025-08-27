package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {
    Optional<TagEntity> findByName(String name);
    @Query("""
    SELECT t
    FROM TagEntity t
    JOIN t.capsules c
    WHERE c.id = :capsuleId
""")
    List<TagEntity> findTagsByCapsuleId(@Param("capsuleId") UUID capsuleId);

}
