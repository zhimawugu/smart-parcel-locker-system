package com.smartparcel.locker.dto;

import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.enums.LockerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLockerRequest {
    @NotBlank
    private String code;

    @NotNull
    private LockerSize size;
    private LockerStatus status;
}
