package com.smartparcel.locker.entity;

import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.enums.LockerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "locker",
        uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "code"}))
@Getter
public class Locker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Setter
    @Column(nullable = false)
    private String code;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerSize size;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerStatus status;

    /**
     * When the door was opened for placement; used to reclaim abandoned open lockers.
     */
    @Setter
    @Column(name = "door_opened_at")
    private Instant doorOpenedAt;

    protected Locker() {
        // required by JPA
    }

    public Locker(Long stationId, String code, LockerSize size, LockerStatus status) {
        this.stationId = stationId;
        this.code = code;
        this.size = size;
        this.status = status;
    }
}
