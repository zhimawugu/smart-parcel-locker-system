package com.smartparcel.locker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "locker_station")
@Getter
public class LockerStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false, unique = true)
    private String code;

    @Setter
    @Column
    private String address;

    protected LockerStation() {
    }

    public LockerStation(String name, String code, String address) {
        this.name = name;
        this.code = code;
        this.address = address;
    }
}
