package com.smartparcel.locker.dto;

import com.smartparcel.locker.enums.LockerSize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreParcelRequest {
    @NotNull
    private Long stationId;

    @NotBlank
    @Email
    private String recipientEmail;

    @NotNull
    private LockerSize size;

    private String description;
}
