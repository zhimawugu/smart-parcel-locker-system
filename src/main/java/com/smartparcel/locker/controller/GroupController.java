package com.smartparcel.locker.controller;

import com.smartparcel.locker.dto.AddMemberRequest;
import com.smartparcel.locker.dto.CreateGroupRequest;
import com.smartparcel.locker.service.GroupService;
import com.smartparcel.locker.vo.ApiResponse;
import com.smartparcel.locker.vo.GroupMemberResponse;
import com.smartparcel.locker.vo.GroupResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Resource
    private GroupService groupService;

    @PostMapping
    public ApiResponse<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return ApiResponse.success(GroupResponse.from(groupService.createGroup(request)));
    }

    @PostMapping("/{groupId}/members")
    public ApiResponse<GroupMemberResponse> addMember(@PathVariable Long groupId,
                                                      @Valid @RequestBody AddMemberRequest request) {
        return ApiResponse.success(GroupMemberResponse.from(groupService.addMember(groupId, request)));
    }

    @DeleteMapping("/{groupId}/members")
    public ApiResponse<Void> removeMember(@PathVariable Long groupId, @RequestParam String email) {
        groupService.removeMember(groupId, email);
        return ApiResponse.success(null);
    }

    @GetMapping("/{groupId}/members")
    public ApiResponse<List<GroupMemberResponse>> listMembers(@PathVariable Long groupId) {
        List<GroupMemberResponse> members = groupService.listMembers(groupId).stream()
                .map(GroupMemberResponse::from)
                .toList();
        return ApiResponse.success(members);
    }
}
