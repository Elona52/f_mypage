show databases;

CREATE DATABASE mypage;

	use mypage;

-- 내정보 관리
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 고유 ID',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '로그인 이메일',
    password VARCHAR(500) NOT NULL COMMENT '비밀번호(암호화 저장)',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    phone VARCHAR(20) COMMENT '전화번호',
    role ENUM('USER', 'INSTRUCTOR', 'MANAGER') DEFAULT 'USER' COMMENT '회원 권한',
    point INT DEFAULT 0 COMMENT '보유 포인트',
    marketing_agree BOOLEAN DEFAULT FALSE COMMENT '마케팅 수신 동의 여부',
    marketing_agree_date DATETIME DEFAULT NULL COMMENT '마케팅 동의 일시',
    joindate TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입일'
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='사용자 정보';

INSERT INTO user (email, password, name, phone, role, point, marketing_agree, marketing_agree_date)
VALUES
('user1@example.com', 'e3afed0047b08059d0fada10f400c1e5', '김지민', '010-1111-1111', 'USER', 1200, TRUE, '2024-01-12 10:20:00'),
('user2@example.com', '5baa61e4c9b93f3f0682250b6cf8331b', '박서준', '010-2222-2222', 'USER', 500, FALSE, NULL),
('instructor1@example.com', '2bb80d537b1da3e38bd30361aa855686', '이강사', '010-3333-3333', 'INSTRUCTOR', 0, TRUE, '2024-02-01 09:00:00'),
('user3@example.com', '6ca1b8cd4c2cb00da1f5d8ef1c9d35e2', '최유진', '010-4444-4444', 'USER', 300, FALSE, NULL),
('manager1@example.com', '7c4a8d09ca3762af61e59520943dc264', '관리자', '010-5555-5555', 'MANAGER', 0, TRUE, '2024-03-10 14:00:00'),
('user4@example.com', '8f14e45fceea167a5a36dedd4bea2543', '정우성', '010-6666-6666', 'USER', 1800, TRUE, '2024-04-20 11:30:00'),
('user5@example.com', '45c48cce2e2d7fbdea1afc51c7c6ad26', '한예슬', '010-7777-7777', 'USER', 760, FALSE, NULL),
('instructor2@example.com', 'd3d9446802a44259755d38e6d163e820', '유강사', '010-8888-8888', 'INSTRUCTOR', 0, TRUE, '2024-05-03 16:10:00'),
('user6@example.com', 'a87ff679a2f3e71d9181a67b7542122c', '이민호', '010-9999-9999', 'USER', 950, TRUE, '2024-06-18 08:50:00'),
('user7@example.com', 'e4da3b7fbbce2345d7772b0674a318d5', '장원영', '010-0000-0000', 'USER', 40, FALSE, NULL);

-- 예약관리
ALTER TABLE user
ADD COLUMN reservation_status ENUM('NONE', 'RESERVED', 'CANCELLED')
    DEFAULT 'NONE'
    COMMENT '예약 상태';



-- 지점관리 
CREATE TABLE IF NOT EXISTS branch (
    branch_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '지점 ID',
    name VARCHAR(100) NOT NULL COMMENT '지점 이름',
    address VARCHAR(255) DEFAULT NULL COMMENT '지점 주소',
    phone VARCHAR(20) DEFAULT NULL COMMENT '지점 연락처',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시'
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='지점 정보';

INSERT INTO branch (name, address, phone)
VALUES
('강남점', '서울시 강남구 테헤란로 123', '02-111-2222'),
('홍대점', '서울시 마포구 동교로 52', '02-333-4444'),
('잠실점', '서울시 송파구 올림픽로 88', '02-555-6666');


-- 공지관리 
CREATE TABLE IF NOT EXISTS notice (
    notice_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '공지 ID',
    branch_id BIGINT NULL COMMENT '지점 ID (본사 공지는 NULL)',

    category VARCHAR(50) NOT NULL COMMENT '공지 카테고리',
    title VARCHAR(200) NOT NULL COMMENT '공지 제목',
    content TEXT NOT NULL COMMENT '공지 내용',

    is_pinned BOOLEAN DEFAULT FALSE COMMENT '상단 고정 여부',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '공지 등록 일시',

    CONSTRAINT fk_notice_branch
        FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
        ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='공지사항 테이블';
    
INSERT INTO notice (branch_id, category, title, content, is_pinned, created_at)
VALUES
-- ① 본사 공지 (상단 고정)
(NULL, '전체공지', '시스템 점검 안내', '전체 시스템 점검이 예정되어 있습니다. 이용에 참고 부탁드립니다.', TRUE, '2024-10-01 09:00:00'),

-- ② 지점 공지
(1, '이벤트', '10월 신규 회원 이벤트', '신규 가입 회원 대상 특별 포인트 지급 이벤트 진행 중입니다.', FALSE, '2024-10-02 11:00:00'),

-- ③ 본사 공지
(NULL, '업데이트', '앱 기능 개선 안내', '운동 데이터 자동 연동 기능이 업데이트되었습니다.', FALSE, '2024-10-05 14:30:00'),

-- ④ 지점 공지
(2, '시설공지', '러닝머신 교체 안내', '본 지점 러닝머신 3대가 신규 장비로 교체됩니다.', FALSE, '2024-10-06 08:20:00'),

-- ⑤ 지점 공지 (상단 고정)
(3, '강사공지', 'PT 강사 일정 변경', '김강사님의 PT 일정이 일부 변경되었습니다.', TRUE, '2024-10-07 10:10:00'),

-- ⑥ 본사 공지
(NULL, '정책', '마케팅 수신 정책 안내', '새로운 마케팅 수신 정책이 적용됩니다.', FALSE, '2024-10-10 16:00:00'),

-- ⑦ 지점 공지
(1, '운영공지', '샤워실 점검 안내', '샤워실 배수관 점검으로 2시간 동안 이용이 제한됩니다.', FALSE, '2024-10-12 12:40:00'),

-- ⑧ 지점 공지
(2, '수업공지', '요가 클래스 오픈', '매주 금요일 오후 7시 요가 클래스가 새롭게 시작됩니다.', FALSE, '2024-10-13 09:30:00'),

-- ⑨ 본사 공지
(NULL, '전체공지', '회원 정보 보호 안내', '회원 정보 보호를 위해 비밀번호 변경을 권장합니다.', FALSE, '2024-10-15 13:50:00'),

-- ⑩ 지점 공지
(3, '운영공지', '주차장 공사 안내', '주차장 도색 공사가 예정되어 있어 이용이 제한됩니다.', FALSE, '2024-10-18 08:00:00');



CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '결제 고유 ID',

    user_id BIGINT NOT NULL COMMENT '결제한 사용자 ID',

    amount INT NOT NULL COMMENT '결제 금액',

    payment_status ENUM('WAITING_DEPOSIT', 'DEPOSITED', 'FAILED', 'CANCELLED')
        DEFAULT 'WAITING_DEPOSIT'
        COMMENT '무통장 입금 결제 상태',

    payment_method ENUM('BANK')
        DEFAULT 'BANK'
        COMMENT '결제 방법(무통장 입금)',

    payment_type ENUM('ONE_TIME', 'PASS')
        NOT NULL
        COMMENT '결제 타입(단건, 이용권)',

    completed_at DATETIME DEFAULT NULL COMMENT '입금 확인 시간',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '결제 요청 시간',

    CONSTRAINT fk_payment_user FOREIGN KEY (user_id)
        REFERENCES user(user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='무통장 입금 결제 테이블';


INSERT INTO payment 
(user_id, amount, payment_status, payment_method, payment_type, completed_at, created_at)
VALUES
-- ① 입금 대기 중 (단건 결제)
(1, 35000, 'WAITING_DEPOSIT', 'BANK', 'ONE_TIME', NULL, '2024-11-01 10:20:00'),

-- ② 입금 완료(관리자 확인) - 이용권 결제
(2, 150000, 'DEPOSITED', 'BANK', 'PASS', '2024-11-01 13:45:00', '2024-11-01 12:30:00'),

-- ③ 입금 대기 (이용권 결제)
(3, 120000, 'WAITING_DEPOSIT', 'BANK', 'PASS', NULL, '2024-11-02 09:15:00'),

-- ④ 입금 실패 처리됨
(1, 50000, 'FAILED', 'BANK', 'ONE_TIME', NULL, '2024-11-02 10:40:00'),

-- ⑤ 고객 요청으로 취소됨
(4, 80000, 'CANCELLED', 'BANK', 'ONE_TIME', NULL, '2024-11-03 14:10:00'),

-- ⑥ 입금 완료된 단건 결제
(2, 22000, 'DEPOSITED', 'BANK', 'ONE_TIME', '2024-11-04 11:50:00', '2024-11-04 09:00:00');


CREATE TABLE IF NOT EXISTS instructor_detail (
    instructor_id BIGINT PRIMARY KEY COMMENT '강사 ID (USER.user_id 참조)',

    introduction TEXT COMMENT '강사 소개글',
    expertise VARCHAR(255) COMMENT '전문 분야',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',

    -- 외래키
    CONSTRAINT fk_instructor_user
        FOREIGN KEY (instructor_id) REFERENCES user(user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='강사 상세 정보';
    
INSERT INTO instructor_detail (instructor_id, introduction, expertise)
VALUES
(1, '10년 경력의 헬스 트레이너로 체형 교정과 근력 강화 프로그램 전문입니다.', 'PT/체형교정'),
(2, '필라테스 국제 자격증 보유. 부상 방지와 유연성 향상 프로그램을 주로 진행합니다.', '필라테스/유연성'),
(3, '수영 국가대표 후보 출신으로 수영 기초부터 마스터 과정까지 모든 레벨 지도 가능합니다.', '수영/근력강화');




CREATE TABLE IF NOT EXISTS reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '예약 ID',
    user_id BIGINT NOT NULL COMMENT '예약 사용자',
    instructor_id BIGINT NOT NULL COMMENT '강사 ID',

    branch_id BIGINT NOT NULL COMMENT '지점 ID',
    reservation_date DATETIME NOT NULL COMMENT '예약 일시',
    status ENUM('RESERVED', 'CANCELLED', 'COMPLETED')
        DEFAULT 'RESERVED' COMMENT '예약 상태',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',

    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id) REFERENCES user(user_id),

    CONSTRAINT fk_reservation_instructor
        FOREIGN KEY (instructor_id) REFERENCES user(user_id),

    CONSTRAINT fk_reservation_branch
        FOREIGN KEY (branch_id) REFERENCES branch(branch_id)
);

ALTER TABLE reservation 
CONVERT TO CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;


INSERT INTO reservation (user_id, instructor_id, branch_id, reservation_date, status)
VALUES
(1, 3, 1, '2024-11-20 10:00:00', 'COMPLETED'),
(2, 1, 2, '2024-11-21 14:00:00', 'COMPLETED'),
(4, 2, 3, '2024-11-22 09:30:00', 'COMPLETED');

select * from reservation;
SHOW CREATE TABLE reservation;


CREATE TABLE IF NOT EXISTS `review` (
  `review_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '리뷰 ID',
  `reservation_id` BIGINT(20) NOT NULL COMMENT '예약 ID(리뷰는 예약 완료 이후 생성)',
  `user_id` BIGINT(20) NOT NULL COMMENT '리뷰 작성자',
  `instructor_id` BIGINT(20) DEFAULT NULL COMMENT '강사 ID (user.user_id 참조)',
  `rating` TINYINT NOT NULL COMMENT '평점 1~5',
  `content` TEXT COMMENT '리뷰 내용',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
  PRIMARY KEY (`review_id`),
  KEY `idx_review_reservation` (`reservation_id`),
  KEY `idx_review_user` (`user_id`),
  KEY `idx_review_instructor` (`instructor_id`),
  CONSTRAINT `fk_review_reservation`
    FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_user`
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_instructor`
    FOREIGN KEY (`instructor_id`) REFERENCES `user` (`user_id`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



INSERT INTO review (reservation_id, user_id, instructor_id, rating, content)
VALUES
-- 회원 1이 강사 3에게 남긴 리뷰
(1, 1, 3, 5, '강사님이 너무 친절하시고 운동자세를 꼼꼼하게 봐주셨어요!'),

-- 회원 2가 강사 1에게 남긴 리뷰
(2, 2, 1, 4, '수업이 전반적으로 좋았어요. 조금 더 강도가 있었으면 좋겠어요.'),

-- 회원 4가 강사 2에게 남긴 리뷰
(3, 4, 2, 5, '운동 프로그램이 체계적이고 설명이 이해하기 쉬웠어요.'),

-- 같은 강사에게 여러 리뷰가 들어갈 수도 있음
(1, 1, 3, 5, '두 번째 수업도 아주 만족스러웠습니다!'),

-- 실제로는 instructor_id를 null로 둘 수도 있음
(2, 2, NULL, 3, '강사가 바뀌어서 정확히 누군지는 모르겠네요.');

