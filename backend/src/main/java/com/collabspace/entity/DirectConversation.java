package com.collabspace.entity;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="direct_conversations") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DirectConversation { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User userOne; @ManyToOne(fetch=FetchType.LAZY,optional=false) private User userTwo; @Column(nullable=false) @Builder.Default private Instant createdAt=Instant.now(); }