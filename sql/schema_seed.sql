-- MariaDB/MySQL DDL & Seed Data
-- 예약, 거래신청, 히스토리, 리뷰, 출석(Attendance) 테이블과 샘플 데이터

/* =========================================================
 * DDL
 * ========================================================= */

-- 예약 (소프트 삭제 + 결제 상태)
CREATE TABLE IF NOT EXISTS reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    class_id BIGINT,
    facility_id BIGINT,
    payment_status VARCHAR(20) DEFAULT '대기중', -- 대기중 / 결제완료
    deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL
);

-- 이용권 거래 신청
CREATE TABLE IF NOT EXISTS trade_request (
    trade_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    seller_user_id BIGINT NOT NULL,
    buyer_user_id BIGINT,
    price INT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING / APPROVED / REJECTED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 이용내역(History)
CREATE TABLE IF NOT EXISTS history (
    history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    reservation_id BIGINT,
    trade_id BIGINT,
    action VARCHAR(50),      -- 예: TRADE_CREATED, TRADE_STATUS_APPROVED
    detail VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 리뷰
CREATE TABLE IF NOT EXISTS review (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    instructor_id BIGINT,
    reservation_id BIGINT NOT NULL,
    rating INT,
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 출석(Attendance) : 일/주/월 집계용
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    attended_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


/* =========================================================
 * Seed Data
 * ========================================================= */

-- 예약 (2건 결제완료, 1건 대기중+삭제)
INSERT INTO reservation (user_id, class_id, facility_id, payment_status, deleted, created_at, deleted_at) VALUES
(1, 101, 201, '결제완료', FALSE, '2024-01-15 10:30:00', NULL),  -- id 1
(1, 102, 202, '결제완료', FALSE, '2024-01-14 14:20:00', NULL),  -- id 2
(2, 103, 203, '대기중',   TRUE,  '2024-01-13 09:15:00', '2024-01-13 10:00:00'); -- id 3 (삭제됨)

-- 거래 신청
INSERT INTO trade_request (reservation_id, seller_user_id, buyer_user_id, price, status, created_at, updated_at) VALUES
(2, 1, 15, 50000, 'APPROVED', '2024-01-20 12:00:00', '2024-01-20 12:10:00'), -- trade_id 1
(1, 1, 16, 45000, 'PENDING',  '2024-01-21 09:00:00', '2024-01-21 09:00:00'); -- trade_id 2

-- History (거래 생성/승인 기록)
INSERT INTO history (user_id, reservation_id, trade_id, action, detail, created_at) VALUES
(1, 2, 1, 'TRADE_CREATED', '거래 신청이 생성되었습니다.', '2024-01-20 12:00:00'),
(1, 2, 1, 'TRADE_STATUS_APPROVED', '거래 상태가 승인되었습니다.', '2024-01-20 12:10:00'),
(1, 1, 2, 'TRADE_CREATED', '거래 신청이 생성되었습니다.', '2024-01-21 09:00:00');

-- 리뷰 (결제완료 + 히스토리 있는 예약만 조회 대상)
INSERT INTO review (user_id, instructor_id, reservation_id, rating, content, created_at) VALUES
(1, 9001, 1, 5, '좋은 강의였습니다.', '2024-01-16 11:00:00'),
(1, 9002, 2, 4, '만족스러웠습니다.', '2024-01-17 15:30:00');

-- 출석(Attendance): 일/주/월 집계용 예시
INSERT INTO attendance (user_id, reservation_id, attended_at, created_at) VALUES
(1, 1, '2024-01-22 08:00:00', '2024-01-22 08:00:00'), -- 이번 주/일간
(1, 1, '2024-01-23 08:00:00', '2024-01-23 08:00:00'),
(1, 2, '2024-01-17 07:30:00', '2024-01-17 07:30:00'), -- 지난 주
(1, 2, '2024-01-05 07:30:00', '2024-01-05 07:30:00'); -- 지난 달


/* =========================================================
 * Useful Queries (참고)
 * ========================================================= */

-- 리뷰: 결제완료 + 미삭제 + history 존재하는 예약만
-- (현재 ReviewMapper.xml 필터와 일치)
SELECT *
FROM review rev
WHERE EXISTS (
    SELECT 1 FROM reservation r
     WHERE r.reservation_id = rev.reservation_id
       AND r.payment_status = '결제완료'
       AND (r.deleted = FALSE OR r.deleted IS NULL)
)
AND EXISTS (
    SELECT 1 FROM history h
     WHERE h.reservation_id = rev.reservation_id
)
ORDER BY rev.created_at DESC;

-- 이용중 수강권 (결제완료 + 미삭제)
SELECT * FROM reservation
WHERE payment_status = '결제완료'
  AND (deleted = FALSE OR deleted IS NULL)
ORDER BY created_at DESC;

-- 사용자별 history
SELECT * FROM history WHERE user_id = 1 ORDER BY created_at DESC;

-- 일간 출석 집계 (오늘)
SELECT DATE(attended_at) AS day, COUNT(*) AS attend_count
FROM attendance a
JOIN reservation r ON r.reservation_id = a.reservation_id
WHERE a.user_id = 1
  AND r.payment_status = '결제완료' AND (r.deleted = FALSE OR r.deleted IS NULL)
  AND attended_at >= CURDATE()
GROUP BY day;

-- 주간 출석 집계 (이번 주 월~일)
SELECT COUNT(*) AS attend_count
FROM attendance a
JOIN reservation r ON r.reservation_id = a.reservation_id
WHERE a.user_id = 1
  AND r.payment_status = '결제완료' AND (r.deleted = FALSE OR r.deleted IS NULL)
  AND attended_at >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)
  AND attended_at <  DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 7 DAY);

-- 월간 출석 집계 (이번 달)
SELECT COUNT(*) AS attend_count
FROM attendance a
JOIN reservation r ON r.reservation_id = a.reservation_id
WHERE a.user_id = 1
  AND r.payment_status = '결제완료' AND (r.deleted = FALSE OR r.deleted IS NULL)
  AND attended_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
  AND attended_at <  DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH);

