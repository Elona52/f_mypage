package com.project.app.review.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.project.app.review.dto.ReviewDto;

@Mapper
public interface ReviewMapper {
    /**
     * 사용자 ID로 리뷰 조회 (나의 리뷰)
     */
    List<ReviewDto> selectReviewsByUserId(String userId);
    
    /**
     * 리뷰 ID로 조회
     */
    ReviewDto selectReviewById(Long reviewId);
    
    /**
     * 예약 ID로 리뷰 조회
     */
    List<ReviewDto> selectReviewByReservationId(Long reservationId);
    
    /**
     * 예약 ID로 예약 정보 조회 (권한 체크용)
     * @param reservationId 예약 ID
     * @return 예약 정보 (usr_id, payment_status 포함)
     */
    Map<String, Object> selectReservationById(Long reservationId);
    
    /**
     * 리뷰 생성
     */
    void insertReview(ReviewDto reviewDto);
    
    /**
     * 리뷰 수정
     */
    int updateReview(ReviewDto reviewDto);
    
    /**
     * 리뷰 삭제
     */
    int deleteReviewById(Long reviewId);
}

