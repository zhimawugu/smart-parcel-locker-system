package com.smartparcel.locker.vo;

import com.smartparcel.locker.entity.LockerStation;

public record LockerStationResponse(Long id, String name, String code, String address) {
    public static LockerStationResponse from(LockerStation station) {
        return new LockerStationResponse(
                station.getId(),
                station.getName(),
                station.getCode(),
                station.getAddress());
    }
}
