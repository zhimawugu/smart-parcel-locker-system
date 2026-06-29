package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.CollectionGroup;

import java.util.Optional;

public interface CollectionGroupDao {
    CollectionGroup save(CollectionGroup group);

    Optional<CollectionGroup> findById(Long id);
}
