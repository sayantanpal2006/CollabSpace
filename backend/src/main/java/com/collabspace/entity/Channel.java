package com.collabspace.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "channels", indexes = {
        @Index(name = "idx_channel_workspace", columnList = "workspace_id"),
        @Index(name = "idx_channel_category", columnList = "category_id")
})
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChannelCategory category;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false)
    private boolean privateChannel = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public ChannelCategory getCategory() {
        return category;
    }

    public void setCategory(ChannelCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivateChannel() {
        return privateChannel;
    }

    public void setPrivateChannel(boolean privateChannel) {
        this.privateChannel = privateChannel;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Channel x = new Channel();

        public Builder id(UUID v) {
            x.id = v;
            return this;
        }

        public Builder workspace(Workspace v) {
            x.workspace = v;
            return this;
        }

        public Builder category(ChannelCategory v) {
            x.category = v;
            return this;
        }

        public Builder name(String v) {
            x.name = v;
            return this;
        }

        public Builder description(String v) {
            x.description = v;
            return this;
        }

        public Builder privateChannel(boolean v) {
            x.privateChannel = v;
            return this;
        }

        public Builder createdBy(User v) {
            x.createdBy = v;
            return this;
        }

        public Builder createdAt(Instant v) {
            x.createdAt = v;
            return this;
        }

        public Builder updatedAt(Instant v) {
            x.updatedAt = v;
            return this;
        }

        public Channel build() {
            return x;
        }
    }
}
