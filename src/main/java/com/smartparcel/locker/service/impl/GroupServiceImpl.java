package com.smartparcel.locker.service.impl;

import com.smartparcel.locker.dao.CollectionGroupDao;
import com.smartparcel.locker.dao.GroupMemberDao;
import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.AddMemberRequest;
import com.smartparcel.locker.dto.CreateGroupRequest;
import com.smartparcel.locker.entity.CollectionGroup;
import com.smartparcel.locker.entity.GroupMember;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.service.GroupService;
import com.smartparcel.locker.vo.ResultCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private UserDao userDao;
    @Resource
    private CollectionGroupDao groupDao;
    @Resource
    private GroupMemberDao groupMemberDao;

    @Override
    @Transactional
    public CollectionGroup createGroup(CreateGroupRequest request) {
        User owner = userDao.findByEmail(request.getOwnerEmail())
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_FOUND));
        return groupDao.save(new CollectionGroup(owner.getId(), request.getName()));
    }

    @Override
    @Transactional
    public GroupMember addMember(Long groupId, AddMemberRequest request) {
        groupDao.findById(groupId).orElseThrow(() -> new BizException(ResultCode.GROUP_NOT_FOUND));
        User user = userDao.findByEmail(request.getEmail())
                .orElseThrow(() -> new BizException(ResultCode.USER_NOT_FOUND));
        if (groupMemberDao.existsByGroupIdAndUserId(groupId, user.getId())) {
            throw new BizException(ResultCode.DUPLICATE_MEMBER);
        }
        return groupMemberDao.save(new GroupMember(groupId, user.getId()));
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, String email) {
        groupDao.findById(groupId).orElseThrow(() -> new BizException(ResultCode.GROUP_NOT_FOUND));
        User user = userDao.findByEmail(email).orElseThrow(() -> new BizException(ResultCode.USER_NOT_FOUND));
        groupMemberDao.findByGroupIdAndUserId(groupId, user.getId())
                .ifPresent(groupMemberDao::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMember> listMembers(Long groupId) {
        groupDao.findById(groupId).orElseThrow(() -> new BizException(ResultCode.GROUP_NOT_FOUND));
        return groupMemberDao.findByGroupId(groupId);
    }
}
