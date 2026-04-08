package com.minetrace.minetrace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MineRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String location;

    private String province;
    private String district;
    private String licenseNumber;

    @NotBlank
    private String organizationId;

    private String organizationName;
}
