package com.smartparcel.locker.vo;

import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.Parcel;

public record OpenLockerResponse(Long parcelId, Long lockerId, String lockerCode) {
    public static OpenLockerResponse of(Parcel parcel, Locker locker) {
        return new OpenLockerResponse(parcel.getId(), locker.getId(), locker.getCode());
    }
}
