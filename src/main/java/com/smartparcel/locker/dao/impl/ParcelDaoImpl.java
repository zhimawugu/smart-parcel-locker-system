package com.smartparcel.locker.dao.impl;

import com.smartparcel.locker.dao.ParcelDao;
import com.smartparcel.locker.entity.Parcel;
import com.smartparcel.locker.enums.ParcelStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ParcelDaoImpl implements ParcelDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Parcel save(Parcel parcel) {
        if (parcel.getId() == null) {
            em.persist(parcel);
            return parcel;
        }
        return em.merge(parcel);
    }

    @Override
    public Optional<Parcel> findById(Long id) {
        return Optional.ofNullable(em.find(Parcel.class, id));
    }

    @Override
    public Optional<Parcel> findByLockerIdAndStatus(Long lockerId, ParcelStatus status) {
        return em.createQuery(
                        "select p from Parcel p where p.lockerId = :lockerId and p.status = :status",
                        Parcel.class)
                .setParameter("lockerId", lockerId)
                .setParameter("status", status)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Parcel> findByCollectionCode(String collectionCode) {
        return em.createQuery(
                        "select p from Parcel p where p.collectionCode = :code", Parcel.class)
                .setParameter("code", collectionCode)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public void delete(Parcel parcel) {
        em.remove(em.contains(parcel) ? parcel : em.merge(parcel));
    }

    @Override
    public boolean existsByCollectionCode(String collectionCode) {
        Long count = em.createQuery(
                        "select count(p) from Parcel p where p.collectionCode = :code", Long.class)
                .setParameter("code", collectionCode)
                .getSingleResult();
        return count != null && count > 0;
    }
}
