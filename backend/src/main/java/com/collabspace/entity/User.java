package com.collabspace.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="users", indexes={@Index(name="idx_users_email", columnList="email"),@Index(name="idx_users_username", columnList="username")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
  @Column(nullable=false,unique=true,length=40) private String username;
  @Column(nullable=false,unique=true,length=120) private String email;
  @Column(nullable=false) private String password;
  private String profilePicture;
  @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private PresenceStatus status=PresenceStatus.OFFLINE;
  private Instant lastSeen;
  @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private Role role=Role.USER;
  @Column(nullable=false,updatable=false) @Builder.Default private Instant createdAt=Instant.now();
  @Column(nullable=false) @Builder.Default private Instant updatedAt=Instant.now();
  @PreUpdate void touch(){updatedAt=Instant.now();}
}