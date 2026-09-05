package com.collabspace.repository;

import com.collabspace.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    List<Channel> findByWorkspaceIdOrderByNameAsc(UUID workspaceId);

    Optional<Channel> findByWorkspaceIdAndNameIgnoreCase(UUID workspaceId, String name);
}
