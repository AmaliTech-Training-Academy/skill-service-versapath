package com.capstone.skill_service.repository;

import com.capstone.skill_service.model.ClusterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClusterRepository extends JpaRepository<ClusterEntity, UUID> {
    Optional<ClusterEntity> findByName(String name);
}
