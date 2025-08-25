package com.capstone.skill_service.model;

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
@Table(name = "growth_tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrowthTrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "growthTrack", cascade = CascadeType.ALL, orphanRemoval = true) // just remove child from growth track list
    @OrderBy("sequenceOrder ASC") // to always display capsule in sequence order
    private List<TrackCapsuleMappingEntity> skillCapsules = new ArrayList<>();

    private int estimatedMonths;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
