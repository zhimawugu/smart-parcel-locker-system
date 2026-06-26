package com.smartparcel.locker.service;

import com.smartparcel.locker.dto.StoreParcelRequest;
import com.smartparcel.locker.exception.LockerNotOpenException;
import com.smartparcel.locker.exception.NoLockerAvailableException;
import com.smartparcel.locker.exception.ParcelNotFoundException;
import com.smartparcel.locker.exception.RecipientNotFoundException;
import com.smartparcel.locker.exception.StationNotFoundException;
import com.smartparcel.locker.vo.OpenLockerResponse;

public interface ParcelService {
    OpenLockerResponse openLocker(StoreParcelRequest request);

    void closeLocker(Long parcelId);

    void cancel(Long parcelId);
}
