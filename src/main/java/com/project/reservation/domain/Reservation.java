package com.project.reservation.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reservation {
    private Long rsvId; // 예약 ID
    private String usrId; // 회원(사용자) ID
    private Long schdId; // 스케줄 ID
    private Long tktId; // 사용이용권 ID
    private String sttsCd; // 상태코드
    private LocalDateTime regDt; // 등록일시
    private String cnclRsn; // 취소/변경사유(관리자용)
    private String modUsrId; // 수정자 ID
}

