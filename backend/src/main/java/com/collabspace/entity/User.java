package com.collabspace.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="users", indexes={@Index(name="idx_users_email", columnList="email"),@Index(name="idx_users_username", columnList="username")})
public class User {
  @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
  @Column(nullable=false,unique=true,length=40) private String username;
  @Column(nullable=false,unique=true,length=120) private String email;
  @Column(nullable=false) private String password;
  private String profilePicture;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private PresenceStatus status=PresenceStatus.OFFLINE;
  private Instant lastSeen;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.USER;
  @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
  @Column(nullable=false) private Instant updatedAt=Instant.now();
  @PreUpdate void touch(){updatedAt=Instant.now();}

  public User() {}
  public User(UUID id,String username,String email,String password,String profilePicture,PresenceStatus status,Instant lastSeen,Role role,Instant createdAt,Instant updatedAt){this.id=id;this.username=username;this.email=email;this.password=password;this.profilePicture=profilePicture;this.status=status;this.lastSeen=lastSeen;this.role=role;this.createdAt=createdAt;this.updatedAt=updatedAt;}
  public UUID getId(){return id;} public void setId(UUID v){id=v;}
  public String getUsername(){return username;} public void setUsername(String v){username=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;}
  public String getPassword(){return password;} public void setPassword(String v){password=v;}
  public String getProfilePicture(){return profilePicture;} public void setProfilePicture(String v){profilePicture=v;}
  public PresenceStatus getStatus(){return status;} public void setStatus(PresenceStatus v){status=v;}
  public Instant getLastSeen(){return lastSeen;} public void setLastSeen(Instant v){lastSeen=v;}
  public Role getRole(){return role;} public void setRole(Role v){role=v;}
  public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
  public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
  public static Builder builder(){return new Builder();}
  public static class Builder{
    private final User x=new User();
    public Builder id(UUID v){x.id=v;return this;} public Builder username(String v){x.username=v;return this;} public Builder email(String v){x.email=v;return this;}
    public Builder password(String v){x.password=v;return this;} public Builder profilePicture(String v){x.profilePicture=v;return this;} public Builder status(PresenceStatus v){x.status=v;return this;}
    public Builder lastSeen(Instant v){x.lastSeen=v;return this;} public Builder role(Role v){x.role=v;return this;} public Builder createdAt(Instant v){x.createdAt=v;return this;} public Builder updatedAt(Instant v){x.updatedAt=v;return this;}
    public User build(){return x;}
  }
}