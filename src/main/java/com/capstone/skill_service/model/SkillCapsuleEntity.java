package com.capstone.skill_service.model;

import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skill_capsules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillCapsuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String categoryType;
    private String difficulty;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiencyLevel;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true) // just remove child from capsule list
    private List<CapsuleAtomMappingEntity> skillAtoms = new ArrayList<>();

    private int moodleCourseId; // id from moodle database on course table
    private int estimatedHours;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
