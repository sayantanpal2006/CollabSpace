package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="channel_members",uniqueConstraints=@UniqueConstraint(name="uk_channel_user",columnNames={"channel_id","user_id"}),indexes=@Index(name="idx_cm_channel",columnList="channel_id"))
public class ChannelMember { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) private Channel channel; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User user; @Column(nullable=false) private Instant joinedAt=Instant.now(); 
 public ChannelMember() {}
 public ChannelMember(UUID id, Channel channel, User user, Instant joinedAt) { this.id=id; this.channel=channel; this.user=user; this.joinedAt=joinedAt; }
public UUID getId(){return id;} public void setId(UUID v){id=v;} public Channel getChannel(){return channel;} public void setChannel(Channel v){channel=v;} public User getUser(){return user;} public void setUser(User v){user=v;} public Instant getJoinedAt(){return joinedAt;} public void setJoinedAt(Instant v){joinedAt=v;}
 public static Builder builder(){return new Builder();}
 public static class Builder{ private final ChannelMember x=new ChannelMember(); public Builder id(UUID v){x.id=v;return this;} public Builder channel(Channel v){x.channel=v;return this;} public Builder user(User v){x.user=v;return this;} public Builder joinedAt(Instant v){x.joinedAt=v;return this;} public ChannelMember build(){return x;} }
}
