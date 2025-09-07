package com.capstone.skill_service.controller;

import com.capstone.skill_service.dto.ClientResponseFormatDto;
import com.capstone.skill_service.dto.CustomPageResponse;
import com.capstone.skill_service.dto.atom.AtomRequestDto;
import com.capstone.skill_service.dto.atom.AtomResponseDto;
import com.capstone.skill_service.dto.atom.AtomUpdateRequestDto;
import com.capstone.skill_service.service.AtomService;
import com.capstone.skill_service.util.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("atoms")
@Tag(name = "Atoms Controller", description = "Manage all skill atom's api")
public class AtomController {
    private final AtomService atomService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create skill atom", description = "This end point allows only admin to create a skill atom")
    public ResponseEntity<ClientResponseFormatDto> createAtom(
            @Valid @RequestBody AtomRequestDto atomRequestDto
    ) {
        AtomResponseDto savedAtom = this.atomService.create(atomRequestDto);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atom created successfully!")
                .errors(null)
                .data(Map.of("item", savedAtom))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @Operation(summary = "Retrieve skill atoms", description = "This end point allows anyone to fetch all skill atoms")
    public ResponseEntity<ClientResponseFormatDto> fetchAllAtoms(Pageable pageable) {
        CustomPageResponse<AtomResponseDto> atoms = this.atomService.findAll(pageable);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Fetch all atoms")
                .errors(null)
                .data(atoms)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{atomId}")
    @Operation(summary = "Retrieve skill atoms", description = "This end point allows anyone to fetch all skill atoms")
    public ResponseEntity<ClientResponseFormatDto> retrieveSingleAtom(@PathVariable UUID atomId) {
        AtomResponseDto atom = this.atomService.getAtom(atomId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atom retrieved successfully")
                .errors(null)
                .data(Map.of("item", atom))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(name = "delete_atom", path = "/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete Atom",
            description = "The atom is deleted using its id that is passed as a parameter")
    public ResponseEntity<ClientResponseFormatDto> deleteAtom(@PathVariable UUID id){
        this.atomService.deleteById(id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atom deleted successfully!")
                .errors(null)
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{atomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update skill atom", description = "This end point allows admin to update a skill atom")
    public ResponseEntity<ClientResponseFormatDto> updateAtom(
            @Valid @RequestBody AtomUpdateRequestDto atomRequestDto, @PathVariable UUID atomId) {
        AtomResponseDto updatedAtom = this.atomService.partialUpdate(atomRequestDto, atomId);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atom updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedAtom))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update skill atom status", description = "This end point allows admin to update a skill atom as a soft delete")
    public ResponseEntity<ClientResponseFormatDto> updateAtomStatus(
            @RequestParam UUID id, @RequestParam Status status) {
        AtomResponseDto updatedAtom = this.atomService.updateStatus(status, id);
        ClientResponseFormatDto response = ClientResponseFormatDto.builder()
                .success(true)
                .message("Atom status updated successfully!")
                .errors(null)
                .data(Map.of("item", updatedAtom))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
