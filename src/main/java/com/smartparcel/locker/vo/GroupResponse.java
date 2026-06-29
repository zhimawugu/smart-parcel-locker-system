package com.smartparcel.locker.vo;

import com.smartparcel.locker.entity.CollectionGroup;

public record GroupResponse(Long id, Long ownerId, String name) {
    public static GroupResponse from(CollectionGroup group) {
        return new GroupResponse(group.getId(), group.getOwnerId(), group.getName());
    }
}
