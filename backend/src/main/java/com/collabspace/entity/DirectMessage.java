package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="direct_messages",indexes=@Index(name="idx_dm_conversation_created",columnList="conversation_id,created_at"))
public class DirectMessage {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) private DirectConversation conversation;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) private User sender;
 @Column(nullable=false,length=4000) private String content;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @Column(nullable=false) private Instant updatedAt=Instant.now();
 @Column(nullable=false) private boolean edited=false;
 @Column(nullable=false) private boolean deleted=false;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ReadStatus readStatus=ReadStatus.SENT;
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public DirectMessage(){} public DirectMessage(UUID id,DirectConversation conversation,User sender,String content,Instant createdAt,Instant updatedAt,boolean edited,boolean deleted,ReadStatus readStatus){this.id=id;this.conversation=conversation;this.sender=sender;this.content=content;this.createdAt=createdAt;this.updatedAt=updatedAt;this.edited=edited;this.deleted=deleted;this.readStatus=readStatus;}
 public UUID getId(){return id;} public void setId(UUID v){id=v;} public DirectConversation getConversation(){return conversation;} public void setConversation(DirectConversation v){conversation=v;} public User getSender(){return sender;} public void setSender(User v){sender=v;} public String getContent(){return content;} public void setContent(String v){content=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;} public boolean isEdited(){return edited;} public void setEdited(boolean v){edited=v;} public boolean isDeleted(){return deleted;} public void setDeleted(boolean v){deleted=v;} public ReadStatus getReadStatus(){return readStatus;} public void setReadStatus(ReadStatus v){readStatus=v;}
 public static Builder builder(){return new Builder();} public static class Builder{private final DirectMessage x=new DirectMessage(); public Builder id(UUID v){x.id=v;return this;} public Builder conversation(DirectConversation v){x.conversation=v;return this;} public Builder sender(User v){x.sender=v;return this;} public Builder content(String v){x.content=v;return this;} public Builder createdAt(Instant v){x.createdAt=v;return this;} public Builder updatedAt(Instant v){x.updatedAt=v;return this;} public Builder edited(boolean v){x.edited=v;return this;} public Builder deleted(boolean v){x.deleted=v;return this;} public Builder readStatus(ReadStatus v){x.readStatus=v;return this;} public DirectMessage build(){return x;}}
}