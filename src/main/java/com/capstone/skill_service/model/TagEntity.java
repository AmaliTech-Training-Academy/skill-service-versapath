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
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String type;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
