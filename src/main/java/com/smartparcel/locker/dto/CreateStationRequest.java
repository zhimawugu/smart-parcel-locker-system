package com.smartparcel.locker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStationRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String code;
    private String address;
}
