package com.capstone.skill_service.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseFormValidationErrorDto {
    private Boolean success;
    private String message;
    private Object data;
    private List<String> errors;
}
