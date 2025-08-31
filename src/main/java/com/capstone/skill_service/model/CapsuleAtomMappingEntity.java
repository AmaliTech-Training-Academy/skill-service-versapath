package com.capstone.skill_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "capsule_atoms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapsuleAtomMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_capsule_id")
    private SkillCapsuleEntity capsule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_atom_id")
    private SkillAtomEntity atom;

    @Column(nullable = false)
    private Integer sequenceOrder;

}
