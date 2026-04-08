package com.minetrace.minetrace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MineResponse {
    private String id;
    private String name;
    private String location;
    private String province;
    private String district;
    private String licenseNumber;
    private String organizationId;
    private String organizationName;
    private boolean active;
    private String createdAt;
}
