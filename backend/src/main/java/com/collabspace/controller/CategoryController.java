package com.collabspace.controller;

import com.collabspace.dto.ChannelDtos.CategoryResponse;
import com.collabspace.dto.ChannelDtos.CreateCategoryRequest;
import com.collabspace.dto.ChannelDtos.UpdateCategoryRequest;
import com.collabspace.service.ChannelCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workspaces/{workspaceId}/categories")
public class CategoryController {
    private final ChannelCategoryService service;

    @GetMapping
    public List<CategoryResponse> list(@PathVariable UUID workspaceId) {
        return service.list(workspaceId);
    }

    @PostMapping
    public CategoryResponse create(@PathVariable UUID workspaceId, @Valid @RequestBody CreateCategoryRequest request) {
        return service.create(workspaceId, request);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return service.update(workspaceId, categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable UUID workspaceId, @PathVariable UUID categoryId) {
        service.delete(workspaceId, categoryId);
    }
}
