package com.smartparcel.locker.dao;

import com.smartparcel.locker.dao.impl.ParcelDaoImpl;
import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.LockerStation;
import com.smartparcel.locker.entity.Parcel;
import com.smartparcel.locker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import static com.smartparcel.locker.enums.LockerSize.SMALL;
import static com.smartparcel.locker.enums.LockerStatus.OCCUPIED;
import static com.smartparcel.locker.enums.ParcelStatus.WAITING_FOR_COLLECTION;
import static com.smartparcel.locker.enums.Role.RESIDENT;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ParcelDaoImpl.class)
class ParcelDaoImplTest {
    @Autowired
    private ParcelDao parcelDao;
    @Autowired
    private TestEntityManager em;
    @Test
    void saveAssignsIdAndExistsByCollectionCodeReflectsPersistence() {
        User recipient = em.persist(new User("alice@example.com", "HASH", "Alice", RESIDENT));
        LockerStation station = em.persist(new LockerStation("A", "ST-A", "addr"));
        Locker locker = em.persist(new Locker(station.getId(), "S-01", SMALL, OCCUPIED));
        em.flush();

        Parcel saved = parcelDao.save(
                new Parcel(recipient.getId(), locker.getId(), SMALL, "482910", WAITING_FOR_COLLECTION, "a book"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(parcelDao.existsByCollectionCode("482910")).isTrue();
        assertThat(parcelDao.existsByCollectionCode("000000")).isFalse();
    }
}
