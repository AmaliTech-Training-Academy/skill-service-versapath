package com.capstone.skill_service.model;

import com.capstone.skill_service.util.ProficiencyLevel;
import com.capstone.skill_service.util.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

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

    @Column(columnDefinition = "TEXT")
    private String objectives;

    private String categoryType;
    private String difficulty;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel proficiencyLevel;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, orphanRemoval = true) // just remove child from capsule list
    @OrderBy("sequenceOrder ASC") // to always display atoms in sequence order
    private List<CapsuleAtomMappingEntity> skillAtoms = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }) // auto removing association when capsule is deleted
    @JoinTable(
            name = "capsule_tags", // join table name
            joinColumns = @JoinColumn(name = "skill_capsule_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags = new HashSet<>(); // to avoid duplicate

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }) // auto removing association when capsule is deleted
    @JoinTable(
            name = "capsule_clusters", // join table name
            joinColumns = @JoinColumn(name = "skill_capsule_id"),
            inverseJoinColumns = @JoinColumn(name = "cluster_id")
    )
    private Set<ClusterEntity> clusters = new HashSet<>(); // to avoid duplicate

    private int moodleCourseId; // id from moodle database on course table
    private int estimatedHours;
    private String image;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
