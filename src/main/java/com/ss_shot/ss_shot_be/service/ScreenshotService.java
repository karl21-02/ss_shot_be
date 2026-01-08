package com.ss_shot.ss_shot_be.service;

import com.ss_shot.ss_shot_be.dto.request.ScreenshotUpdateRequest;
import com.ss_shot.ss_shot_be.dto.response.ScreenshotListResponse;
import com.ss_shot.ss_shot_be.dto.response.ScreenshotResponse;
import com.ss_shot.ss_shot_be.dto.response.ScreenshotUpdateResponse;
import com.ss_shot.ss_shot_be.dto.response.SearchResponse;
import com.ss_shot.ss_shot_be.entity.Category;
import com.ss_shot.ss_shot_be.entity.ScreenshotMetadata;
import com.ss_shot.ss_shot_be.exception.BusinessException;
import com.ss_shot.ss_shot_be.exception.ErrorCode;
import com.ss_shot.ss_shot_be.repository.ScreenshotMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenshotService {

    private final ScreenshotMetadataRepository metadataRepository;

    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int SEARCH_DEFAULT_SIZE = 50;

    @Transactional(readOnly = true)
    public ScreenshotListResponse getScreenshots(Long userId, String category, int page, int size, String sort) {
        size = Math.min(size <= 0 ? DEFAULT_PAGE_SIZE : size, MAX_PAGE_SIZE);
        Pageable pageable = createPageable(page, size, sort);

        Page<ScreenshotMetadata> metadataPage;

        if (category != null && !category.isBlank()) {
            try {
                Category cat = Category.valueOf(category.toUpperCase());
                metadataPage = metadataRepository.findByUserIdAndCategoryAndIsDeletedFalse(userId, cat, pageable);
            } catch (IllegalArgumentException e) {
                metadataPage = metadataRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
            }
        } else {
            metadataPage = metadataRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
        }

        Page<ScreenshotResponse> responsePage = metadataPage.map(ScreenshotResponse::from);
        return ScreenshotListResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public SearchResponse search(Long userId, String query, String category, int page, int size) {
        if (query == null || query.length() < 2) {
            throw new BusinessException(ErrorCode.SEARCH_001);
        }

        size = Math.min(size <= 0 ? SEARCH_DEFAULT_SIZE : size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size);

        Page<ScreenshotMetadata> resultPage;

        if (category != null && !category.isBlank()) {
            resultPage = metadataRepository.searchByFullTextAndCategory(userId, query, category.toUpperCase(), pageable);
        } else {
            resultPage = metadataRepository.searchByFullTextWithFallback(userId, query, pageable);
        }

        List<SearchResponse.SearchResultItem> items = resultPage.getContent().stream()
                .map(metadata -> SearchResponse.SearchResultItem.from(metadata, query))
                .toList();

        return SearchResponse.builder()
                .content(items)
                .query(query)
                .totalElements(resultPage.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }

    @Transactional
    public ScreenshotUpdateResponse updateScreenshot(Long userId, Long screenshotId, ScreenshotUpdateRequest request) {
        ScreenshotMetadata metadata = metadataRepository.findByIdAndUserId(screenshotId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCREENSHOT_NOT_FOUND));

        if (request.getIsDeleted() != null) {
            metadata.setIsDeleted(request.getIsDeleted());
        }

        if (request.getIsFavorite() != null) {
            metadata.setIsFavorite(request.getIsFavorite());
        }

        metadata = metadataRepository.save(metadata);
        return ScreenshotUpdateResponse.from(metadata);
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "capturedAt");

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                String field = parts[0].trim();
                String direction = parts[1].trim().toLowerCase();
                Sort.Direction dir = "asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
                sortOrder = Sort.by(dir, field);
            }
        }

        return PageRequest.of(page, size, sortOrder);
    }
}
