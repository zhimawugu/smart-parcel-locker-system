package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.Locker;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LockerDao {
    Locker save(Locker locker);

    Optional<Locker> findById(Long id);

    List<Locker> findAvailableByStation(Long stationId);

    List<Locker> findExpiredDoorOpen(Instant threshold);
}
