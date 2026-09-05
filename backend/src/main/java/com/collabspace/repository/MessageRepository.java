package com.collabspace.repository;

import com.collabspace.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("select m from Message m where m.channel.id=:channelId and m.deleted=false order by m.createdAt desc")
    Page<Message> findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(@Param("channelId") UUID channelId, Pageable p);

    Page<Message> findByChannel_Workspace_IdAndContentContainingIgnoreCaseAndDeletedFalse(UUID workspaceId, String q, Pageable p);
}
