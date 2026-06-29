package com.smartparcel.locker.vo;

import com.smartparcel.locker.entity.GroupMember;

public record GroupMemberResponse(Long id, Long groupId, Long userId) {
    public static GroupMemberResponse from(GroupMember member) {
        return new GroupMemberResponse(member.getId(), member.getGroupId(), member.getUserId());
    }
}
