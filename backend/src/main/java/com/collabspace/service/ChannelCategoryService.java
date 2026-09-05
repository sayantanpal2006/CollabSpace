package com.collabspace.service;

import com.collabspace.dto.ChannelDtos.CategoryResponse;
import com.collabspace.dto.ChannelDtos.CreateCategoryRequest;
import com.collabspace.dto.ChannelDtos.UpdateCategoryRequest;
import com.collabspace.entity.ChannelCategory;
import com.collabspace.entity.WorkspaceMember;
import com.collabspace.entity.WorkspaceRole;
import com.collabspace.repository.ChannelCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelCategoryService {
    private final ChannelCategoryRepository categories;
    private final WorkspaceService workspaces;

    public List<CategoryResponse> list(UUID workspaceId) {
        workspaces.requireMember(workspaceId);
        return categories.findByWorkspaceIdOrderByPositionAscNameAsc(workspaceId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CategoryResponse create(UUID workspaceId, CreateCategoryRequest request) {
        requireWorkspaceAdmin(workspaceId);
        if (categories.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, request.name().trim())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        ChannelCategory category = new ChannelCategory();
        category.setWorkspace(workspaces.getEntity(workspaceId));
        category.setName(request.name().trim());
        category.setPosition(request.position());
        return toDto(categories.save(category));
    }

    @Transactional
    public CategoryResponse update(UUID workspaceId, UUID categoryId, UpdateCategoryRequest request) {
        requireWorkspaceAdmin(workspaceId);
        ChannelCategory category = categories.findByIdAndWorkspaceId(categoryId, workspaceId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        category.setName(request.name().trim());
        category.setPosition(request.position());
        return toDto(category);
    }

    @Transactional
    public void delete(UUID workspaceId, UUID categoryId) {
        requireWorkspaceAdmin(workspaceId);
        ChannelCategory category = categories.findByIdAndWorkspaceId(categoryId, workspaceId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
        categories.delete(category);
    }

    public ChannelCategory getForWorkspace(UUID workspaceId, UUID categoryId) {
        return categories.findByIdAndWorkspaceId(categoryId, workspaceId)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
    }

    private void requireWorkspaceAdmin(UUID workspaceId) {
        WorkspaceMember member = workspaces.member(workspaceId);
        if (member.getRole() == WorkspaceRole.MEMBER) {
            throw new SecurityException("Admin permission required");
        }
    }

    private CategoryResponse toDto(ChannelCategory c) {
        return new CategoryResponse(c.getId(), c.getWorkspace().getId(), c.getName(), c.getPosition(), c.getCreatedAt());
    }
}
