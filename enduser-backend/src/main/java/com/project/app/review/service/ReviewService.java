package com.project.app.review.service;

import java.util.List;

import com.project.app.review.dto.ReviewDto;

public interface ReviewService {
    /**
     * 나의 리뷰 목록 조회
     * @param userId 사용자 ID
     * @return 리뷰 목록
     */
    List<ReviewDto> getMyReviews(String userId);
    
    /**
     * 예약 ID로 리뷰 조회 (권한 체크 포함)
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @return 리뷰 목록
     */
    List<ReviewDto> getReviewByReservationId(Long reservationId, String userId);
    
    /**
     * 리뷰 생성 (권한 체크 포함)
     * @param reviewDto 생성할 리뷰 정보
     * @param userId 사용자 ID
     * @return 생성된 리뷰 정보
     */
    ReviewDto createReview(ReviewDto reviewDto, String userId);
    
    /**
     * 리뷰 수정 (권한 체크 포함)
     * @param reviewDto 수정할 리뷰 정보
     * @param userId 사용자 ID
     * @return 수정된 리뷰 정보
     */
    ReviewDto updateReview(ReviewDto reviewDto, String userId);
    
    /**
     * 리뷰 삭제 (권한 체크 포함)
     * @param reviewId 삭제할 리뷰 ID
     * @param userId 사용자 ID
     */
    void deleteReviewById(Long reviewId, String userId);
}

