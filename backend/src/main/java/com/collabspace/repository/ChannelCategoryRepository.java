package com.collabspace.repository;

import com.collabspace.entity.ChannelCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelCategoryRepository extends JpaRepository<ChannelCategory, UUID> {
    List<ChannelCategory> findByWorkspaceIdOrderByPositionAscNameAsc(UUID workspaceId);

    Optional<ChannelCategory> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    boolean existsByWorkspaceIdAndNameIgnoreCase(UUID workspaceId, String name);
}
