package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="message_reactions",uniqueConstraints=@UniqueConstraint(name="uk_reaction",columnNames={"message_id","user_id","emoji"}))
public class MessageReaction { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) private Message message; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User user; @Column(nullable=false,length=16) private String emoji; @Column(nullable=false).Default private Instant createdAt=Instant.now(); 
 public MessageReaction() {}
 public MessageReaction(UUID id, Message message, User user, String emoji, Instant createdAt) { this.id=id; this.message=message; this.user=user; this.emoji=emoji; this.createdAt=createdAt; }
public UUID getId(){return id;} public void setId(UUID v){id=v;} public Message getMessage(){return message;} public void setMessage(Message v){message=v;} public User getUser(){return user;} public void setUser(User v){user=v;} public String getEmoji(){return emoji;} public void setEmoji(String v){emoji=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
 public static Builder builder(){return new Builder();}
 public static class Builder{ private final MessageReaction x=new MessageReaction(); public Builder id(UUID v){x.id=v;return this;} public Builder message(Message v){x.message=v;return this;} public Builder user(User v){x.user=v;return this;} public Builder emoji(String v){x.emoji=v;return this;} public Builder createdAt(Instant v){x.createdAt=v;return this;} public MessageReaction build(){return x;} }
}
