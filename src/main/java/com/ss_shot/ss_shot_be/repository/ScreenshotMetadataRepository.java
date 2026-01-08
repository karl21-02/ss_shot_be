package com.ss_shot.ss_shot_be.repository;

import com.ss_shot.ss_shot_be.entity.Category;
import com.ss_shot.ss_shot_be.entity.ScreenshotMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenshotMetadataRepository extends JpaRepository<ScreenshotMetadata, Long> {

    Optional<ScreenshotMetadata> findByUserIdAndLocalId(Long userId, String localId);

    Optional<ScreenshotMetadata> findByIdAndUserId(Long id, Long userId);

    Page<ScreenshotMetadata> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    Page<ScreenshotMetadata> findByUserIdAndCategoryAndIsDeletedFalse(
            Long userId, Category category, Pageable pageable);

    @Query(value = """
            SELECT * FROM screenshot_metadata
            WHERE user_id = :userId
              AND is_deleted = FALSE
              AND to_tsvector('simple', COALESCE(full_text, '')) @@ plainto_tsquery('simple', :query)
            ORDER BY captured_at DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM screenshot_metadata
            WHERE user_id = :userId
              AND is_deleted = FALSE
              AND to_tsvector('simple', COALESCE(full_text, '')) @@ plainto_tsquery('simple', :query)
            """,
            nativeQuery = true)
    Page<ScreenshotMetadata> searchByFullText(
            @Param("userId") Long userId,
            @Param("query") String query,
            Pageable pageable);

    @Query(value = """
            SELECT * FROM screenshot_metadata
            WHERE user_id = :userId
              AND is_deleted = FALSE
              AND (
                to_tsvector('simple', COALESCE(full_text, '')) @@ plainto_tsquery('simple', :query)
                OR full_text ILIKE '%' || :query || '%'
              )
            ORDER BY captured_at DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM screenshot_metadata
            WHERE user_id = :userId
              AND is_deleted = FALSE
              AND (
                to_tsvector('simple', COALESCE(full_text, '')) @@ plainto_tsquery('simple', :query)
                OR full_text ILIKE '%' || :query || '%'
              )
            """,
            nativeQuery = true)
    Page<ScreenshotMetadata> searchByFullTextWithFallback(
            @Param("userId") Long userId,
            @Param("query") String query,
            Pageable pageable);

    @Query(value = """
            SELECT * FROM screenshot_metadata
            WHERE user_id = :userId
              AND category = :category
              AND is_deleted = FALSE
              AND (
                to_tsvector('simple', COALESCE(full_text, '')) @@ plainto_tsquery('simple', :query)
                OR full_text ILIKE '%' || :query || '%'
              )
            ORDER BY captured_at DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM screenshot_metadata
            WHERE user_id = :userId
              AND category = :category
              AND is_deleted = FALSE
              AND (
                to_tsvector('simple', COALESCE(full_text, '')) @@ plainto_tsquery('simple', :query)
                OR full_text ILIKE '%' || :query || '%'
              )
            """,
            nativeQuery = true)
    Page<ScreenshotMetadata> searchByFullTextAndCategory(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("category") String category,
            Pageable pageable);

    @Query("SELECT sm FROM ScreenshotMetadata sm WHERE sm.user.id = :userId AND sm.localId IN :localIds")
    List<ScreenshotMetadata> findByUserIdAndLocalIdIn(
            @Param("userId") Long userId,
            @Param("localIds") List<String> localIds);

    // 통계 쿼리
    @Query("SELECT COUNT(sm) FROM ScreenshotMetadata sm WHERE sm.user.id = :userId AND sm.isDeleted = false")
    long countByUserIdAndNotDeleted(@Param("userId") Long userId);

    @Query("SELECT sm.category, COUNT(sm) FROM ScreenshotMetadata sm " +
           "WHERE sm.user.id = :userId AND sm.isDeleted = false " +
           "GROUP BY sm.category")
    List<Object[]> countByUserIdGroupByCategory(@Param("userId") Long userId);

    @Query("SELECT COUNT(sm) FROM ScreenshotMetadata sm " +
           "WHERE sm.user.id = :userId AND sm.isDeleted = false " +
           "AND sm.createdAt >= :startDate")
    long countAddedThisMonth(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(sm) FROM ScreenshotMetadata sm " +
           "WHERE sm.user.id = :userId AND sm.isDeleted = true " +
           "AND sm.updatedAt >= :startDate")
    long countDeletedThisMonth(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
