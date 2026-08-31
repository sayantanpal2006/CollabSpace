package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="workspaces")
public class Workspace { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(nullable=false,length=80) private String name; @Column(length=500) private String description; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User owner; @Column(nullable=false).Default private Instant createdAt=Instant.now(); @Column(nullable=false).Default private Instant updatedAt=Instant.now(); @PreUpdate void touch(){updatedAt=Instant.now();} 
 public Workspace() {}
 public Workspace(UUID id, String name, String description, User owner, Instant createdAt, Instant updatedAt) { this.id=id; this.name=name; this.description=description; this.owner=owner; this.createdAt=createdAt; this.updatedAt=updatedAt; }
public UUID getId(){return id;} public void setId(UUID v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public User getOwner(){return owner;} public void setOwner(User v){owner=v;} public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
 public static Builder builder(){return new Builder();}
 public static class Builder{ private final Workspace x=new Workspace(); public Builder id(UUID v){x.id=v;return this;} public Builder name(String v){x.name=v;return this;} public Builder description(String v){x.description=v;return this;} public Builder owner(User v){x.owner=v;return this;} public Builder createdAt(Instant v){x.createdAt=v;return this;} public Builder updatedAt(Instant v){x.updatedAt=v;return this;} public Workspace build(){return x;} }
}
