package com.smartparcel.locker.service;

import com.smartparcel.locker.dto.CollectParcelRequest;
import com.smartparcel.locker.dto.StoreParcelRequest;
import com.smartparcel.locker.vo.CollectInfoResponse;
import com.smartparcel.locker.vo.OpenLockerResponse;

public interface ParcelService {
    OpenLockerResponse openLocker(StoreParcelRequest request);

    void closeLocker(Long parcelId);

    void cancel(Long parcelId);

    CollectInfoResponse collect(CollectParcelRequest request);

    void collectDone(Long parcelId);
}
