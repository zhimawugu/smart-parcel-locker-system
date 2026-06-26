package com.smartparcel.locker.entity;

import com.smartparcel.locker.enums.LockerSize;
import com.smartparcel.locker.enums.ParcelStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "parcel")
@Getter
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Setter
    @Column(name = "locker_id", nullable = false)
    private Long lockerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerSize size;

    @Column(name = "collection_code", nullable = false, unique = true)
    private String collectionCode;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @Setter
    @Column
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Parcel() {

    }

    public Parcel(Long recipientId, Long lockerId, LockerSize size, String collectionCode,
                  ParcelStatus status, String description) {
        this.recipientId = recipientId;
        this.lockerId = lockerId;
        this.size = size;
        this.collectionCode = collectionCode;
        this.status = status;
        this.description = description;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
