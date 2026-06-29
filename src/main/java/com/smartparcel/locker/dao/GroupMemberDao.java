package com.smartparcel.locker.dao;

import com.smartparcel.locker.entity.GroupMember;

import java.util.List;
import java.util.Optional;

public interface GroupMemberDao {
    GroupMember save(GroupMember member);

    List<GroupMember> findByGroupId(Long groupId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    void delete(GroupMember member);
}
