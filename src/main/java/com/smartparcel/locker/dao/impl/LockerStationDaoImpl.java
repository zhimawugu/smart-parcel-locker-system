package com.smartparcel.locker.dao.impl;

import com.smartparcel.locker.dao.LockerStationDao;
import com.smartparcel.locker.entity.LockerStation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LockerStationDaoImpl implements LockerStationDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public LockerStation save(LockerStation station) {
        if (station.getId() == null) {
            em.persist(station);
            return station;
        }
        return em.merge(station);
    }

    @Override
    public Optional<LockerStation> findById(Long id) {
        return Optional.ofNullable(em.find(LockerStation.class, id));
    }

    @Override
    public List<LockerStation> findAll() {
        return em.createQuery("select s from LockerStation s order by s.id", LockerStation.class)
                .getResultList();
    }
}
