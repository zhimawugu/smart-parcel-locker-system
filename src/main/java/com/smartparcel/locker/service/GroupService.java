package com.smartparcel.locker.service;

import com.smartparcel.locker.dto.AddMemberRequest;
import com.smartparcel.locker.dto.CreateGroupRequest;
import com.smartparcel.locker.entity.CollectionGroup;
import com.smartparcel.locker.entity.GroupMember;

import java.util.List;

public interface GroupService {
    CollectionGroup createGroup(CreateGroupRequest request);

    GroupMember addMember(Long groupId, AddMemberRequest request);

    void removeMember(Long groupId, String email);

    List<GroupMember> listMembers(Long groupId);
}
