package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.Parcel;
import com.smartparcel.locker.enums.ParcelStatus;

import java.util.Optional;

public interface ParcelDao {
    Parcel save(Parcel parcel);

    Optional<Parcel> findById(Long id);

    Optional<Parcel> findByLockerIdAndStatus(Long lockerId, ParcelStatus status);

    Optional<Parcel> findByCollectionCode(String collectionCode);

    void delete(Parcel parcel);

    boolean existsByCollectionCode(String collectionCode);
}
