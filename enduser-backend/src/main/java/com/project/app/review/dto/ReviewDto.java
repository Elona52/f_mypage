package com.project.app.review.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getter, setter, equals, hashCode, toString 자동 생성
@NoArgsConstructor      // 파라미터 없는 기본 생성자
@AllArgsConstructor     // 모든 필드를 파라미터로 받는 생성자
@Builder                // 빌더 패턴 구현
public class ReviewDto {
    private Long reviewId;          // rvw_id: 리뷰 ID
    private Long reservationId;     // rsv_id: 예약 ID
    private Integer rating;          // rating: 별점
    private String content;          // content: 후기 내용
    private Long instructorId;       // inst_id: 강사 ID
    private LocalDateTime registrationDateTime; // reg_dt: 작성일
    
    // 예약/스케줄 정보 (JOIN으로 가져옴)
    private LocalDateTime exerciseDate;      // exercise_date: 운동 날짜/시간
    private String exerciseName;             // exercise_name: 운동명
    private String programName;              // program_name: 프로그램명 (없으면 exerciseName 사용)
    private String trainerName;              // trainer_name: 트레이너 이름
    private String exerciseLocation;        // exercise_location: 운동 장소
    private String branchName;               // branch_name: 지점명 (없으면 exerciseLocation 사용)
}

