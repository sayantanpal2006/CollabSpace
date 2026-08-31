package com.collabspace.entity;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="workspace_members",uniqueConstraints=@UniqueConstraint(name="uk_workspace_user",columnNames={"workspace_id","user_id"}),indexes={@Index(name="idx_wm_workspace",columnList="workspace_id"),@Index(name="idx_wm_user",columnList="user_id")})
public class WorkspaceMember { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) private Workspace workspace; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User user; @Enumerated(EnumType.STRING) @Column(nullable=false) private WorkspaceRole role; @Column(nullable=false).Default private Instant joinedAt=Instant.now(); 
 public WorkspaceMember() {}
 public WorkspaceMember(UUID id, Workspace workspace, User user, WorkspaceRole role, Instant joinedAt) { this.id=id; this.workspace=workspace; this.user=user; this.role=role; this.joinedAt=joinedAt; }
public UUID getId(){return id;} public void setId(UUID v){id=v;} public Workspace getWorkspace(){return workspace;} public void setWorkspace(Workspace v){workspace=v;} public User getUser(){return user;} public void setUser(User v){user=v;} public WorkspaceRole getRole(){return role;} public void setRole(WorkspaceRole v){role=v;} public Instant getJoinedAt(){return joinedAt;} public void setJoinedAt(Instant v){joinedAt=v;}
 public static Builder builder(){return new Builder();}
 public static class Builder{ private final WorkspaceMember x=new WorkspaceMember(); public Builder id(UUID v){x.id=v;return this;} public Builder workspace(Workspace v){x.workspace=v;return this;} public Builder user(User v){x.user=v;return this;} public Builder role(WorkspaceRole v){x.role=v;return this;} public Builder joinedAt(Instant v){x.joinedAt=v;return this;} public WorkspaceMember build(){return x;} }
}
