package com.capstone.skill_service.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_capsule_id")
    private SkillCapsuleEntity capsule;

    @ManyToOne(optional = false)
    @JoinColumn(name = "skill_atom_id")
    private SkillAtomEntity atom;

    @Column(nullable = false)
    private Integer sequenceOrder;

}
