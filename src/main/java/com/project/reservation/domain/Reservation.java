package com.project.reservation.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long id;
    private Long userId;
    private Long classId;
    private Long facilityId;
    private String paymentStatus; // 결제 현황: "대기중", "결제완료"
    private Boolean deleted; // 삭제 여부: false(활성), true(삭제됨)
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt; // 삭제 시간
}

