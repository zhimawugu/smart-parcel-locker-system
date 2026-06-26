package com.smartparcel.locker.vo;

import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.enums.LockerStatus;

public record LockerResponse(Long id, Long stationId, String code, LockerSize size, LockerStatus status) {
    public static LockerResponse from(Locker locker) {
        return new LockerResponse(
                locker.getId(),
                locker.getStationId(),
                locker.getCode(),
                locker.getSize(),
                locker.getStatus());
    }
}
