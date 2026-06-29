package com.smartparcel.locker.dao.impl;

import com.smartparcel.locker.dao.CollectionGroupDao;
import com.smartparcel.locker.entity.CollectionGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CollectionGroupDaoImpl implements CollectionGroupDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public CollectionGroup save(CollectionGroup group) {
        if (group.getId() == null) {
            em.persist(group);
            return group;
        }
        return em.merge(group);
    }

    @Override
    public Optional<CollectionGroup> findById(Long id) {
        return Optional.ofNullable(em.find(CollectionGroup.class, id));
    }
}
