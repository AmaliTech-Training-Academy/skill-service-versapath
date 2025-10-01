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
@Table(name = "talent_routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentRouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String roleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String image;

    @OneToMany(mappedBy = "talentRoute", cascade = CascadeType.ALL, orphanRemoval = true) // just remove child from talent route list
    @OrderBy("sequenceOrder ASC") // to always display growth track in sequence order
    private List<RouteTrackMappingEntity> tracks = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
