package com.capstone.skill_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "route_tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteTrackMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "talent_route_id")
    private TalentRouteEntity talentRoute;

    @ManyToOne(optional = false)
    @JoinColumn(name = "growth_track_id")
    private GrowthTrackEntity growthTrack;

    @Column(nullable = false)
    private Integer sequenceOrder;

}
