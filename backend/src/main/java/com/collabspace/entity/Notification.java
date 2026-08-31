package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="notifications",indexes=@Index(name="idx_notification_user_created",columnList="user_id,created_at"))
public class Notification { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User user; @Enumerated(EnumType.STRING) @Column(nullable=false) private NotificationType type; @Column(nullable=false,length=500) private String message; @Column(nullable=false) private boolean read=false; @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now(); 
 public Notification() {}
 public Notification(UUID id, User user, NotificationType type, String message, boolean read, Instant createdAt) { this.id=id; this.user=user; this.type=type; this.message=message; this.read=read; this.createdAt=createdAt; }
public UUID getId(){return id;} public void setId(UUID v){id=v;} public User getUser(){return user;} public void setUser(User v){user=v;} public NotificationType getType(){return type;} public void setType(NotificationType v){type=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;} public boolean isRead(){return read;} public void setRead(boolean v){read=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
 public static Builder builder(){return new Builder();}
 public static class Builder{ private final Notification x=new Notification(); public Builder id(UUID v){x.id=v;return this;} public Builder user(User v){x.user=v;return this;} public Builder type(NotificationType v){x.type=v;return this;} public Builder message(String v){x.message=v;return this;} public Builder read(boolean v){x.read=v;return this;} public Builder createdAt(Instant v){x.createdAt=v;return this;} public Notification build(){return x;} }
}
