package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.LockerStation;

import java.util.List;
import java.util.Optional;

public interface LockerStationDao {
    LockerStation save(LockerStation station);

    Optional<LockerStation> findById(Long id);

    List<LockerStation> findAll();
}
