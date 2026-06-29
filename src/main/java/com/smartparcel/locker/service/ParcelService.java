package com.smartparcel.locker.service;

import com.smartparcel.locker.dto.StoreParcelRequest;
import com.smartparcel.locker.vo.OpenLockerResponse;

public interface ParcelService {
    OpenLockerResponse openLocker(StoreParcelRequest request);

    void closeLocker(Long parcelId);

    void cancel(Long parcelId);
}
