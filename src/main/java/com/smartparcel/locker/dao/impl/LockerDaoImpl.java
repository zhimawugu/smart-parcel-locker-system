package com.smartparcel.locker.dao.impl;

import com.smartparcel.locker.dao.LockerDao;
import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.enums.LockerStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class LockerDaoImpl implements LockerDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Locker save(Locker locker) {
        if (locker.getId() == null) {
            em.persist(locker);
            return locker;
        }
        return em.merge(locker);
    }

    @Override
    public Optional<Locker> findById(Long id) {
        return Optional.ofNullable(em.find(Locker.class, id));
    }

    @Override
    public List<Locker> findAvailableByStation(Long stationId) {
        return em.createQuery(
                        "select l from Locker l where l.stationId = :stationId and l.status = :status "
                                + "order by l.id", Locker.class)
                .setParameter("stationId", stationId)
                .setParameter("status", LockerStatus.AVAILABLE)
                .getResultList();
    }

    @Override
    public List<Locker> findExpiredDoorOpen(Instant threshold) {
        return em.createQuery(
                        "select l from Locker l where l.status = :status and l.doorOpenedAt < :threshold",
                        Locker.class)
                .setParameter("status", LockerStatus.DOOR_OPEN)
                .setParameter("threshold", threshold)
                .getResultList();
    }
}
