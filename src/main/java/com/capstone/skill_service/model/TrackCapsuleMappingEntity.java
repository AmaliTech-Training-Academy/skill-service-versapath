package com.capstone.skill_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "track_capsules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackCapsuleMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "growth_track_id")
    private GrowthTrackEntity growthTrack;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_capsule_id")
    private SkillCapsuleEntity capsule;

    @Column(nullable = false)
    private Integer sequenceOrder;

}
