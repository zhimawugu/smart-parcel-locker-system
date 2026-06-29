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
@Table(name = "collection_group")
@Getter
public class CollectionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Setter
    @Column(nullable = false)
    private String name;

    protected CollectionGroup() {
    }

    public CollectionGroup(Long ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }
}
