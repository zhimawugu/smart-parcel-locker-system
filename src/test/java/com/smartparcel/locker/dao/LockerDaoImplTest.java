package com.smartparcel.locker.dao;

import com.smartparcel.locker.dao.impl.LockerDaoImpl;
import com.smartparcel.locker.entity.Locker;
import com.smartparcel.locker.entity.LockerStation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.smartparcel.locker.enums.LockerSize.SMALL;
import static com.smartparcel.locker.enums.LockerStatus.AVAILABLE;
import static com.smartparcel.locker.enums.LockerStatus.OCCUPIED;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(LockerDaoImpl.class)
class LockerDaoImplTest {
    @Autowired
    private LockerDao lockerDao;
    @Autowired
    private TestEntityManager em;
    @Test
    void findAvailableByStationReturnsOnlyAvailableInThatStation() {
        LockerStation station = em.persist(new LockerStation("A", "ST-A", "addr"));
        em.persist(new Locker(station.getId(), "S-01", SMALL, AVAILABLE));
        em.persist(new Locker(station.getId(), "S-02", SMALL, OCCUPIED));
        LockerStation other = em.persist(new LockerStation("B", "ST-B", "addr"));
        em.persist(new Locker(other.getId(), "S-01", SMALL, AVAILABLE));
        em.flush();

        List<Locker> available = lockerDao.findAvailableByStation(station.getId());

        assertThat(available).hasSize(1);
        assertThat(available.get(0).getCode()).isEqualTo("S-01");
        assertThat(available.get(0).getStatus()).isEqualTo(AVAILABLE);
    }
    @Test
    void saveAssignsId() {
        LockerStation station = em.persist(new LockerStation("A", "ST-A", "addr"));
        em.flush();

        Locker saved = lockerDao.save(new Locker(station.getId(), "S-01", SMALL, AVAILABLE));

        assertThat(saved.getId()).isNotNull();
    }
}
