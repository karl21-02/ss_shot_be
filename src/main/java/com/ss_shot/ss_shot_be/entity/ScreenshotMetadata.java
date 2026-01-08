package com.ss_shot.ss_shot_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "screenshot_metadata", indexes = {
    @Index(name = "idx_user_category_captured", columnList = "user_id, category, captured_at DESC"),
    @Index(name = "idx_user_deleted", columnList = "user_id, is_deleted")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_screenshot_user_local", columnNames = {"user_id", "local_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenshotMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "local_id", nullable = false, length = 255)
    private String localId;

    @Column(name = "full_text", columnDefinition = "TEXT")
    private String fullText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ocr_json", columnDefinition = "jsonb")
    private String ocrJson;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private Category category = Category.OTHER;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
