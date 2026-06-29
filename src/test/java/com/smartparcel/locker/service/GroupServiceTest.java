package com.smartparcel.locker.service;

import com.smartparcel.locker.dao.CollectionGroupDao;
import com.smartparcel.locker.dao.GroupMemberDao;
import com.smartparcel.locker.dao.UserDao;
import com.smartparcel.locker.dto.AddMemberRequest;
import com.smartparcel.locker.dto.CreateGroupRequest;
import com.smartparcel.locker.entity.CollectionGroup;
import com.smartparcel.locker.entity.GroupMember;
import com.smartparcel.locker.entity.User;
import com.smartparcel.locker.exception.BizException;
import com.smartparcel.locker.service.impl.GroupServiceImpl;
import com.smartparcel.locker.vo.ResultCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.smartparcel.locker.enums.Role.RESIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private CollectionGroupDao groupDao;
    @Mock
    private GroupMemberDao groupMemberDao;
    @InjectMocks
    private GroupServiceImpl groupService;

    private final User owner = new User("owner@example.com", "HASH", "Owner", RESIDENT);
    private final User member = new User("member@example.com", "HASH", "Member", RESIDENT);

    private CreateGroupRequest createRequest() {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setOwnerEmail("owner@example.com");
        request.setName("Family");
        return request;
    }

    private AddMemberRequest memberRequest() {
        AddMemberRequest request = new AddMemberRequest();
        request.setEmail("member@example.com");
        return request;
    }

    @Test
    void createGroupSavesWithOwner() {
        when(userDao.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(groupDao.save(any(CollectionGroup.class))).then(returnsFirstArg());

        CollectionGroup group = groupService.createGroup(createRequest());

        assertThat(group.getName()).isEqualTo("Family");
    }

    @Test
    void addMemberSavesMembership() {
        when(groupDao.findById(1L)).thenReturn(Optional.of(new CollectionGroup(1L, "Family")));
        when(userDao.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(groupMemberDao.existsByGroupIdAndUserId(eq(1L), any())).thenReturn(false);
        when(groupMemberDao.save(any(GroupMember.class))).then(returnsFirstArg());

        GroupMember saved = groupService.addMember(1L, memberRequest());

        assertThat(saved.getGroupId()).isEqualTo(1L);
    }

    @Test
    void addMemberRejectsDuplicate() {
        when(groupDao.findById(1L)).thenReturn(Optional.of(new CollectionGroup(1L, "Family")));
        when(userDao.findByEmail("member@example.com")).thenReturn(Optional.of(member));
        when(groupMemberDao.existsByGroupIdAndUserId(eq(1L), any())).thenReturn(true);

        assertThatThrownBy(() -> groupService.addMember(1L, memberRequest()))
                .isInstanceOf(BizException.class)
                .hasFieldOrPropertyWithValue("resultCode", ResultCode.DUPLICATE_MEMBER);
    }
}
