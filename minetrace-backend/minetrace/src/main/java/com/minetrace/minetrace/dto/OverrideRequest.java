package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OverrideRequest {
    @NotBlank
    @Size(min = 10)
    private String note;
}
