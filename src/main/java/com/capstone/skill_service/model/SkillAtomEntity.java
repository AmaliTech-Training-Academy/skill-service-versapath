package com.capstone.skill_service.model;

import com.capstone.skill_service.util.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "skill_atoms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillAtomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String objectives;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String ContentType;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int moodleModuleId; // id for tracking progress

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int moodlePageId; // id for fetching content

    private int estimatedHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
