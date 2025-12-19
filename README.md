# 마이페이지 백엔드 API

운동 예약 및 출석 관리 시스템의 백엔드 API 서버입니다.

## 📋 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [주요 기능](#주요-기능)
3. [기술 스택](#기술-스택)
4. [인증 방식](#인증-방식)
5. [API 목록](#api-목록)
6. [시작하기](#시작하기)
7. [데이터베이스 스키마](#데이터베이스-스키마)

---

## 프로젝트 개요

사용자가 운동 예약을 하고, 출석을 체크하며, 리뷰를 작성할 수 있는 마이페이지 기능을 제공하는 REST API 서버입니다.

### 주요 특징

- **JWT 기반 인증**: 모든 API는 JWT 토큰 인증이 필요합니다 (회원가입/로그인 제외)
- **출석 관리**: 기간권(사용자 체크) / 횟수권(강사 체크) 구분
- **출석현황 조회**: 전체 출석현황 조회 지원
- **리뷰 작성**: 결제완료된 예약에 대한 리뷰 작성 기능

---

## 주요 기능

### 1. 마이페이지 기능

#### 예약목록 조회
- 로그인한 사용자의 모든 예약 목록 조회
- 예약 상태별 필터링 (RESERVED, CANCELLED, COMPLETED)

#### 이용목록 조회
- **입금완료된 예약만** 조회
- 결제 상태가 `BANK_TRANSFER_COMPLETED`인 예약만 표시
- 이용내역 화면에서 사용


### 3. 출석 관리

#### 출석체크 방식
- **기간권 (PERIOD)**: 사용자가 직접 출석체크 버튼 클릭
- **횟수권 (COUNT)**: 각 수업의 강사가 출석체크

#### 출석현황 조회
- **일간**: 특정 날짜의 출석현황 조회
- **주간**: 시작일부터 7일간의 출석현황 조회
- **월간**: 특정 년/월의 출석현황 조회

---

## 기술 스택

- **Java**: 21
- **Spring Boot**: 3.x
- **Spring Security**: JWT 인증
- **Spring Data JPA**: 데이터베이스 접근
- **H2 Database**: 테스트용 인메모리 DB
- **Gradle**: 빌드 도구

---

## 인증 방식

### JWT 토큰 인증

모든 API는 JWT 인증 토큰이 필요합니다. (회원가입/로그인 제외)

#### Authorization 헤더 형식
```
Authorization: Bearer {JWT_TOKEN}
```

#### 인증이 필요 없는 API
- `POST /api/auth/login` - 로그인
- `POST /api/user/register` - 회원가입

#### 인증이 필요한 API
- 예약 관련: `/api/reservation/**`
- 출석 관련: `/api/attendance/**`
- 이용권 관련: `/api/ticket/**`
- 리뷰 관련: `/api/reviews/**`
- 사용자 정보: `/api/user/userinfo`
- 기타 모든 API

#### 인증 실패 시
- **401 Unauthorized** 응답
- 메시지: `{"error":"Unauthorized","message":"인증 토큰이 필요합니다."}`

---

## API 목록

### 예약 관련 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/reservation` | 예약 생성 | ✅ |
| GET | `/api/reservation/my` | 나의 예약목록 조회 | ✅ |
| GET | `/api/reservation/my/completed` | 이용목록 조회 (입금완료된 건만) | ✅ |
| GET | `/api/reservation/{reservationId}` | 예약 상세 조회 | ✅ |
| POST | `/api/reservation/{reservationId}/cancel` | 예약 취소 | ✅ |

### 이용권 관련 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/ticket/my` | 나의 이용권 목록 조회 | ✅ |

### 리뷰 관련 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| GET | `/api/reviews/my` | 나의 리뷰 목록 조회 | ✅ |
| GET | `/api/reviews/id/{reviewId}` | 리뷰 상세 조회 | ✅ |
| GET | `/api/reviews/reservation/{reservationId}` | 예약별 리뷰 조회 | ✅ |
| POST | `/api/reviews` | 리뷰 작성 | ✅ |
| PUT | `/api/reviews/{reviewId}` | 리뷰 수정 | ✅ |
| DELETE | `/api/reviews/{reviewId}` | 리뷰 삭제 | ✅ |

**결제 상태 (예약 응답에 포함):**
- `BANK_TRANSFER_PENDING`: 입금대기
- `BANK_TRANSFER_COMPLETED`: 입금완료

### 출석 관련 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/attendance/reservation/{reservationId}/check` | 사용자 출석체크 (기간권) | ✅ |
| GET | `/api/attendance/my` | 나의 출석현황 조회 | ✅ |
| PUT | `/api/attendance/{attendanceId}/status` | 출석 상태 변경 | ✅ |

**출석 상태:**
- `ATTEND`: 출석
- `ABSENT`: 결석
- `CANCEL`: 취소

**이용권 타입:**
- `PERIOD`: 기간권 (사용자가 출석체크)
- `COUNT`: 횟수권 (강사가 출석체크)

### 인증 관련 API

| Method | Endpoint | 설명 | 인증 |
|--------|----------|------|------|
| POST | `/api/auth/login` | 로그인 | ❌ |
| POST | `/api/user/register` | 회원가입 | ❌ |
| GET | `/api/user/userinfo` | 사용자 정보 조회 | ✅ |

---

## 시작하기

### 사전 요구사항

- Java 21 이상
- Gradle 8.x 이상

### 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test
```

### 설정 파일

- `application.properties`: 기본 설정
- `application-dev.properties`: 개발 환경 설정
- `application-prod.properties`: 운영 환경 설정
- `application-test.properties`: 테스트 환경 설정

---

## 데이터베이스 스키마

### 주요 엔티티

#### Reservation (예약)
- 예약 정보 및 상태 관리
- Schedule, Ticket, User와 연관관계


#### Attendance (출석)
- 출석 현황 관리
- Reservation, Ticket, User와 연관관계

#### Schedule (스케줄)
- 운동 클래스 스케줄 정보

#### Ticket (이용권)
- 사용자 이용권 정보
- 타입: PERIOD (기간권), COUNT (횟수권)

### 스키마 상세

자세한 스키마 정보는 `DATABASE_SCHEMA_MAPPING.md` 파일을 참조하세요.

---

## API 명세서

상세한 API 명세는 `API_SPECIFICATION.md` 파일을 참조하세요.

---

## 보안 설정

인증 설정에 대한 자세한 설명은 `SECURITY_CONFIG_EXPLANATION.md` 파일을 참조하세요.

### 주요 보안 설정

- **인증 없이 접근 가능**: 로그인, 회원가입만
- **인증 필요**: 나머지 모든 API
- **JWT 토큰 검증**: 모든 요청에 대해 JWT 토큰 유효성 검사

---

## 개발 가이드

### TDD 개발 방식

이 프로젝트는 TDD (Test-Driven Development) 방식으로 개발되었습니다.

- 테스트 코드 작성 → 구현 → 리팩토링 순서로 진행
- 통합 테스트 및 단위 테스트 포함

자세한 내용은 `TDD_IMPLEMENTATION_SUMMARY.md` 파일을 참조하세요.

---

## 라이선스

이 프로젝트는 내부 사용을 위한 프로젝트입니다.

---

## 문의

프로젝트 관련 문의사항이 있으시면 개발팀에 문의해주세요.
