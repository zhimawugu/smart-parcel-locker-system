package com.smartparcel.locker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank
    @Email
    private String ownerEmail;

    @NotBlank
    private String name;
}
