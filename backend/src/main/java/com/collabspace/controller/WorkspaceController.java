package com.collabspace.controller;

import com.collabspace.dto.WorkspaceDtos.*;
import com.collabspace.service.WorkspaceInviteService;
import com.collabspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {
    private final WorkspaceService service;
    private final WorkspaceInviteService invites;

    @GetMapping
    public List<WorkspaceResponse> list() {
        return service.list();
    }

    @PostMapping
    public WorkspaceResponse create(@Valid @RequestBody CreateWorkspaceRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public WorkspaceResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public WorkspaceResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateWorkspaceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/{id}/members")
    public void addMember(@PathVariable UUID id, @Valid @RequestBody AddMemberRequest request) {
        service.addMember(id, request);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public void removeMember(@PathVariable UUID id, @PathVariable UUID userId) {
        service.removeMember(id, userId);
    }

    @PostMapping("/{id}/invites")
    public WorkspaceInviteResponse createInvite(@PathVariable UUID id, @Valid @RequestBody CreateInviteRequest request) {
        return invites.create(id, request);
    }

    @GetMapping("/{id}/invites")
    public List<WorkspaceInviteResponse> listInvites(@PathVariable UUID id) {
        return invites.list(id);
    }

    @DeleteMapping("/{id}/invites/{inviteId}")
    public void revokeInvite(@PathVariable UUID id, @PathVariable UUID inviteId) {
        invites.revoke(id, inviteId);
    }

    @PostMapping("/invites/accept")
    public WorkspaceInviteResponse acceptInvite(@Valid @RequestBody AcceptInviteRequest request) {
        return invites.accept(request);
    }
}
