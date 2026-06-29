package com.smartparcel.locker.service;

import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.LockerStation;
import com.smartparcel.locker.dto.CreateLockerRequest;
import com.smartparcel.locker.dto.CreateStationRequest;
import com.smartparcel.locker.enums.LockerSize;

import java.util.List;

public interface LockerService {
    LockerStation createStation(CreateStationRequest request);

    Locker createLocker(Long stationId, CreateLockerRequest request);

    List<LockerStation> listStations();

    List<Locker> listAvailableLockers(Long stationId, LockerSize size);
}
