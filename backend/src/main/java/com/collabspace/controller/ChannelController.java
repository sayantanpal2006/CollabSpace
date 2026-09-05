package com.collabspace.controller;

import com.collabspace.dto.ChannelDtos.*;
import com.collabspace.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService service;

    @GetMapping("/api/workspaces/{workspaceId}/channels")
    public List<ChannelResponse> list(@PathVariable UUID workspaceId) {
        return service.list(workspaceId);
    }

    @PostMapping("/api/workspaces/{workspaceId}/channels")
    public ChannelResponse create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateChannelRequest request) {
        return service.create(workspaceId, request);
    }

    @PutMapping("/api/channels/{id}")
    public ChannelResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateChannelRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/api/channels/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/api/channels/{id}/join")
    public void join(@PathVariable UUID id) {
        service.join(id);
    }

    @PostMapping("/api/channels/{id}/leave")
    public void leave(@PathVariable UUID id) {
        service.leave(id);
    }
}
