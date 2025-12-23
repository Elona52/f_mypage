package com.project.app.review.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.review.dto.ReviewDto;
import com.project.app.review.service.ReviewService;

import lombok.extern.slf4j.Slf4j;

/**
 * 리뷰 컨트롤러
 * 리뷰 CRUD (Create, Read, Update, Delete) 기능을 제공하는 REST API
 * 모든 API는 JWT 인증 토큰이 필요합니다.
 * Authorization 헤더에 "Bearer {token}" 형식으로 토큰을 포함해야 합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

	// 리뷰 비즈니스 로직을 처리하는 서비스
    private final ReviewService reviewService;

    // 생성자: ReviewService를 주입받습니다
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    /**
     * 요청 헤더에서 사용자 ID를 가져옵니다.
     * 프론트엔드의 localStorage에서 가져온 userId가 X-User-Id 헤더로 전달됩니다.
     * 
     * @param request HTTP 요청 객체
     * @return 사용자 ID
     */
    /**
     * 나의 리뷰 목록 조회
     * GET /api/reviews/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyReviews(@RequestParam String userId) {
        log.info("[ReviewController] getMyReviews - userId: {}", userId);
        try {
            List<ReviewDto> reviews = reviewService.getMyReviews(userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("리뷰 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("리뷰 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 예약 ID로 리뷰 조회
     * GET /api/reviews/reservation/{reservationId}
     */
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getReviewByReservationId(
            @PathVariable Long reservationId,
            @RequestParam String userId) {
        try {
            List<ReviewDto> reviews = reviewService.getReviewByReservationId(reservationId, userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            log.error("예약별 리뷰 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("예약별 리뷰 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 리뷰 생성
     * POST /api/reviews
     * 
     * 요청 본문 예시:
     * {
     *   "reservationId": 1,
     *   "rating": 5,
     *   "content": "정말 좋은 수업이었습니다!",
     *   "instructorId": 1
     * }
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestParam String userId,
            @RequestBody ReviewDto reviewDto){
        log.info("[ReviewController] createReview - userId: {}, reviewDto: {}", userId, reviewDto);
        try {
            ReviewDto createdReview = reviewService.createReview(reviewDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            log.error("리뷰 생성 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("리뷰 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 리뷰 수정
     * PUT /api/reviews/{reviewId}
     * 헤더: Authorization: Bearer {token}, X-User-Id: {userId}
     * 
     * 요청 본문 예시:
     * {
     *   "rating": 4,
     *   "content": "수정된 후기 내용"
     * }
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestParam String userId,
            @RequestBody ReviewDto reviewDto){
        log.info("[ReviewController] updateReview - reviewId: {}, userId: {}, reviewDto: {}", reviewId, userId, reviewDto);
        try {
            reviewDto.setReviewId(reviewId);
            ReviewDto updatedReview = reviewService.updateReview(reviewDto, userId);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            log.error("리뷰 수정 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("리뷰 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 리뷰 삭제
     * DELETE /api/reviews/{reviewId}
     * 헤더: Authorization: Bearer {token}, X-User-Id: {userId}
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReviewById(
            @PathVariable Long reviewId,
            @RequestParam String userId){
        log.info("[ReviewController] deleteReviewById - reviewId: {}, userId: {}", reviewId, userId);
        try {
            reviewService.deleteReviewById(reviewId, userId);
            return ResponseEntity.ok("리뷰가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("리뷰 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("리뷰 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

