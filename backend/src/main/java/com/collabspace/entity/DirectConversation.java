package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="direct_conversations")
public class DirectConversation {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) private User userOne;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) private User userTwo;
 @Column(nullable=false) private Instant createdAt=Instant.now();
 public DirectConversation(){} public DirectConversation(UUID id,User userOne,User userTwo,Instant createdAt){this.id=id;this.userOne=userOne;this.userTwo=userTwo;this.createdAt=createdAt;}
 public UUID getId(){return id;} public void setId(UUID v){id=v;} public User getUserOne(){return userOne;} public void setUserOne(User v){userOne=v;} public User getUserTwo(){return userTwo;} public void setUserTwo(User v){userTwo=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
 public static Builder builder(){return new Builder();} public static class Builder{private final DirectConversation x=new DirectConversation(); public Builder id(UUID v){x.id=v;return this;} public Builder userOne(User v){x.userOne=v;return this;} public Builder userTwo(User v){x.userTwo=v;return this;} public Builder createdAt(Instant v){x.createdAt=v;return this;} public DirectConversation build(){return x;}}
}