# MyPage 프로젝트 - 예약 관리 시스템

## 📋 프로젝트 개요

Spring Boot 기반의 예약 관리 시스템으로, 사용자의 예약 목록 조회, 결제 현황 관리(대기중/결제완료), 예약 삭제 기능을 제공하는 RESTful API 서버입니다.

## 🛠 기술 스택

- **Java**: 21
- **Spring Boot**: 3.5.9-SNAPSHOT
- **MyBatis**: 3.0.5 (데이터베이스 매핑)
- **MariaDB**: 데이터베이스
- **Lombok**: 보일러플레이트 코드 제거
- **Gradle**: 빌드 도구

## 📁 프로젝트 구조

```
src/main/java/com/project/
├── MyPageApplication.java          # Spring Boot 메인 애플리케이션
├── controller/
│   ├── ReviewController.java       # 리뷰 컨트롤러
│   └── ReviewViewController.java   # 리뷰 뷰 컨트롤러
├── domain/
│   └── Review.java                 # 리뷰 도메인 모델
├── mapper/
│   └── ReviewMapper.java           # 리뷰 MyBatis 매퍼 인터페이스
├── service/
│   └── ReviewService.java          # 리뷰 비즈니스 로직
└── reservation/
    ├── controller/
    │   └── ReservationController.java    # 예약 컨트롤러
    ├── domain/
    │   └── Reservation.java              # 예약 도메인 모델
    ├── mapper/
    │   └── ReservationMapper.java        # 예약 MyBatis 매퍼 인터페이스
    └── service/
        └── ReservationService.java       # 예약 비즈니스 로직

src/main/resources/
├── application.properties          # 애플리케이션 설정
├── mapper/
│   ├── ReviewMapper.xml            # 리뷰 SQL 매핑
│   └── ReservationMapper.xml       # 예약 SQL 매핑
└── static/
    └── index.html                  # 정적 리소스
```

## 🏗 아키텍처 패턴

이 프로젝트는 **3계층 아키텍처(Layered Architecture)** 패턴을 따릅니다:

```
Controller Layer (컨트롤러 계층)
    ↓
Service Layer (서비스 계층)
    ↓
Mapper Layer (데이터 접근 계층)
    ↓
Database (데이터베이스)
```

### 계층별 역할

1. **Controller Layer**: HTTP 요청/응답 처리, RESTful API 엔드포인트 제공
2. **Service Layer**: 비즈니스 로직 처리, 트랜잭션 관리
3. **Mapper Layer**: 데이터베이스 쿼리 실행, 객체-관계 매핑

## 🔄 예약 기능 동작 원리

### 1. 예약 목록 조회 기능

#### 요청 흐름도
```
클라이언트 요청
    ↓
GET /api/reservations/user/{userId}
    ↓
ReservationController.getUserReservations()
    ↓
ReservationService.getReservationsByUser()
    ↓
ReservationMapper.findByUser()
    ↓
ReservationMapper.xml (SQL 실행)
    ↓
MariaDB 데이터베이스
    ↓
결과 반환 (List<Reservation>)
```

#### 상세 동작 과정

1. **클라이언트 요청**
   ```http
   GET /api/reservations/user/1
   ```

2. **Controller 처리** (`ReservationController.java`)
   - `@GetMapping("/user/{userId}")` 어노테이션으로 요청 매핑
   - 경로 변수 `userId` 추출
   - `ReservationService.getReservationsByUser(userId)` 호출

3. **Service 처리** (`ReservationService.java`)
   - `@Service` 어노테이션으로 Spring Bean 등록
   - `@RequiredArgsConstructor`로 `ReservationMapper` 의존성 주입
   - `reservationMapper.findByUser(userId)` 호출

4. **Mapper 처리** (`ReservationMapper.java`)
   - `@Mapper` 어노테이션으로 MyBatis 매퍼 인터페이스 등록
   - `findByUser(Long userId)` 메서드 선언

5. **SQL 실행** (`ReservationMapper.xml`)
   ```xml
   <select id="findByUser" resultMap="reservationMap">
       SELECT * FROM reservation 
       WHERE user_id = #{userId} 
       ORDER BY created_at DESC
   </select>
   ```
   - `resultMap`을 통해 데이터베이스 컬럼을 Java 객체로 매핑
   - `reservation_id` → `id`
   - `user_id` → `userId`
   - `class_id` → `classId`
   - `facility_id` → `facilityId`
   - `payment_status` → `paymentStatus`
   - `deleted` → `deleted`
   - `created_at` → `createdAt`
   - `deleted_at` → `deletedAt`

6. **결과 반환**
   - 데이터베이스에서 조회된 결과를 `List<Reservation>` 객체로 변환
   - JSON 형태로 클라이언트에 응답

### 2. 예약 삭제 기능 (소프트 삭제)

> **소프트 삭제(Soft Delete)** 방식: 실제로 데이터베이스에서 레코드를 삭제하지 않고, `deleted` 플래그를 `true`로 설정하여 논리적으로 삭제 처리합니다. 이 방식의 장점은 데이터 복구 가능, 삭제 이력 추적, 참조 무결성 유지 등입니다.

#### 요청 흐름도
```
클라이언트 요청
    ↓
DELETE /api/reservations/{id}
    ↓
ReservationController.deleteReservation()
    ↓
ReservationService.deleteReservation()
    ↓
ReservationMapper.softDelete()
    ↓
ReservationMapper.xml (UPDATE SQL 실행)
    ↓
MariaDB 데이터베이스
    ↓
업데이트된 행 수 반환
    ↓
성공/실패 응답
```

#### 상세 동작 과정

1. **클라이언트 요청**
   ```http
   DELETE /api/reservations/1
   ```

2. **Controller 처리** (`ReservationController.java`)
   - `@DeleteMapping("/{id}")` 어노테이션으로 요청 매핑
   - 경로 변수 `id` 추출
   - `ReservationService.deleteReservation(id)` 호출
   - 반환된 결과(업데이트된 행 수)를 확인하여 성공/실패 응답

3. **Service 처리** (`ReservationService.java`)
   - `reservationMapper.softDelete(id)` 호출
   - 물리적 삭제가 아닌 **소프트 삭제(논리적 삭제)** 수행
   - 업데이트된 행 수를 반환 (0이면 삭제 실패, 1 이상이면 성공)

4. **Mapper 처리** (`ReservationMapper.java`)
   - `softDelete(Long id)` 메서드 선언

5. **SQL 실행** (`ReservationMapper.xml`)
   ```xml
   <update id="softDelete">
       UPDATE reservation
       SET deleted = true,
           deleted_at = NOW()
       WHERE reservation_id = #{id}
       AND (deleted = false OR deleted IS NULL)
   </update>
   ```
   - 해당 `reservation_id`의 `deleted` 컬럼을 `true`로 변경
   - `deleted_at` 컬럼에 삭제 시간 기록
   - 이미 삭제된 레코드는 다시 삭제할 수 없도록 조건 추가

6. **결과 반환**
   - 삭제 성공 시: `200 OK` + "예약 삭제 완료"
   - 삭제 실패 시: `404 Not Found` (이미 삭제되었거나 존재하지 않는 경우)

#### 소프트 삭제의 특징

- **데이터 보존**: 실제 데이터는 데이터베이스에 유지됨
- **자동 필터링**: 모든 조회 쿼리에서 `deleted = false` 조건이 자동 적용되어 삭제된 예약은 조회되지 않음
- **복구 가능**: 필요시 `deleted` 플래그를 `false`로 변경하여 복구 가능
- **삭제 이력**: `deleted_at` 컬럼으로 삭제 시간 추적 가능

### 3. 결제 상태 변경 기능

#### 요청 흐름도
```
클라이언트 요청
    ↓
PATCH /api/reservations/{id}/payment-status?paymentStatus=결제완료
    ↓
ReservationController.updatePaymentStatus()
    ↓
결제 상태 유효성 검증
    ↓
ReservationService.updatePaymentStatus()
    ↓
ReservationMapper.updatePaymentStatus()
    ↓
ReservationMapper.xml (SQL 실행)
    ↓
MariaDB 데이터베이스
    ↓
업데이트된 행 수 반환
    ↓
성공/실패 응답
```

#### 상세 동작 과정

1. **클라이언트 요청**
   ```http
   PATCH /api/reservations/1/payment-status?paymentStatus=결제완료
   ```

2. **Controller 처리** (`ReservationController.java`)
   - `@PatchMapping("/{id}/payment-status")` 어노테이션으로 요청 매핑
   - 경로 변수 `id`와 쿼리 파라미터 `paymentStatus` 추출
   - 결제 상태 유효성 검증 ("대기중", "결제완료"만 허용)
   - 유효하지 않은 경우 `400 Bad Request` 반환
   - `ReservationService.updatePaymentStatus(id, paymentStatus)` 호출

3. **Service 처리** (`ReservationService.java`)
   - `reservationMapper.updatePaymentStatus(id, paymentStatus)` 호출
   - 업데이트된 행 수를 반환 (0이면 업데이트 실패, 1 이상이면 성공)

4. **Mapper 처리** (`ReservationMapper.java`)
   - `updatePaymentStatus(Long id, String paymentStatus)` 메서드 선언

5. **SQL 실행** (`ReservationMapper.xml`)
   ```xml
   <update id="updatePaymentStatus">
       UPDATE reservation
       SET payment_status = #{paymentStatus}
       WHERE reservation_id = #{id}
   </update>
   ```
   - 해당 `reservation_id`의 `payment_status` 컬럼만 업데이트
   - 다른 필드는 변경하지 않음

6. **결과 반환**
   - 업데이트 성공 시: `200 OK` + "결제 상태가 변경되었습니다: 결제완료"
   - 업데이트 실패 시: `404 Not Found`
   - 유효하지 않은 상태값: `400 Bad Request` + 에러 메시지

### 4. 결제 상태별 조회 기능

#### 요청 흐름도
```
클라이언트 요청
    ↓
GET /api/reservations/payment-status/대기중
    ↓
ReservationController.getReservationsByPaymentStatus()
    ↓
ReservationService.getReservationsByPaymentStatus()
    ↓
ReservationMapper.findByPaymentStatus()
    ↓
ReservationMapper.xml (SQL 실행)
    ↓
MariaDB 데이터베이스
    ↓
결과 반환 (List<Reservation>)
```

#### 상세 동작 과정

1. **클라이언트 요청**
   ```http
   GET /api/reservations/payment-status/대기중
   ```

2. **Controller 처리** (`ReservationController.java`)
   - `@GetMapping("/payment-status/{paymentStatus}")` 어노테이션으로 요청 매핑
   - 경로 변수 `paymentStatus` 추출
   - `ReservationService.getReservationsByPaymentStatus(paymentStatus)` 호출

3. **Service 처리** (`ReservationService.java`)
   - `reservationMapper.findByPaymentStatus(paymentStatus)` 호출

4. **Mapper 처리** (`ReservationMapper.java`)
   - `findByPaymentStatus(String paymentStatus)` 메서드 선언

5. **SQL 실행** (`ReservationMapper.xml`)
   ```xml
   <select id="findByPaymentStatus" resultMap="reservationMap">
       SELECT * FROM reservation 
       WHERE payment_status = #{paymentStatus} 
       ORDER BY created_at DESC
   </select>
   ```
   - `payment_status` 컬럼으로 필터링
   - 생성일 기준 내림차순 정렬

6. **결과 반환**
   - 해당 결제 상태를 가진 예약 목록을 `List<Reservation>` 객체로 변환
   - JSON 형태로 클라이언트에 응답

### 5. 이용중인 수강권/강의내역 조회 기능

#### 요청 흐름도
```
클라이언트 요청
    ↓
GET /api/reservations/user/{userId}/active
    ↓
ReservationController.getActiveReservations()
    ↓
ReservationService.getActiveReservationsByUser()
    ↓
ReservationMapper.findByUserAndPaymentStatus(userId, "결제완료")
    ↓
ReservationMapper.xml (SQL 실행)
    ↓
MariaDB 데이터베이스
    ↓
결과 반환 (List<Reservation> - 결제완료 상태만)
```

#### 상세 동작 과정

1. **클라이언트 요청**
   ```http
   GET /api/reservations/user/1/active
   ```

2. **Controller 처리** (`ReservationController.java`)
   - `@GetMapping("/user/{userId}/active")` 어노테이션으로 요청 매핑
   - 경로 변수 `userId` 추출
   - `ReservationService.getActiveReservationsByUser(userId)` 호출

3. **Service 처리** (`ReservationService.java`)
   - `getActiveReservationsByUser()` 메서드에서 자동으로 "결제완료" 상태 필터링
   - `reservationMapper.findByUserAndPaymentStatus(userId, "결제완료")` 호출

4. **Mapper 처리** (`ReservationMapper.java`)
   - `findByUserAndPaymentStatus(Long userId, String paymentStatus)` 메서드 사용
   - `paymentStatus` 파라미터에 "결제완료" 자동 전달

5. **SQL 실행** (`ReservationMapper.xml`)
   ```xml
   <select id="findByUserAndPaymentStatus" resultMap="reservationMap">
       SELECT * FROM reservation 
       WHERE user_id = #{userId} 
       AND payment_status = #{paymentStatus} 
       AND (deleted = false OR deleted IS NULL)
       ORDER BY created_at DESC
   </select>
   ```
   - 사용자 ID와 결제완료 상태로 필터링
   - 삭제되지 않은 예약만 조회
   - 생성일 기준 내림차순 정렬

6. **결과 반환**
   - 결제완료 상태인 예약만 `List<Reservation>` 객체로 변환
   - JSON 형태로 클라이언트에 응답
   - 이용중인 수강권/강의내역 페이지에 표시할 데이터 제공

#### 특징
- **자동 필터링**: 결제완료 상태인 예약만 자동으로 반환
- **삭제된 예약 제외**: 소프트 삭제된 예약은 자동으로 제외
- **명확한 용도**: 이용중인 수강권/강의내역 페이지 전용 API

## 📡 API 엔드포인트

## 📊 API 정리표

| API 종류 | API 명(영어) | API 명(한글) | 설명 |
|---------|------------|------------|------|
| 예약 | getAllReservations | 전체 예약 목록 조회 | 전체 예약 목록을 조회한다 |
| 예약 | getReservation | 예약 단건 조회 | 예약 ID로 예약 정보를 조회한다 |
| 예약 | getUserReservations | 사용자별 예약 목록 조회 | 특정 사용자의 예약 목록을 조회한다 |
| 예약 | getReservationsByPaymentStatus | 결제 상태별 예약 목록 조회 | 결제 상태별로 예약 목록을 조회한다 |
| 예약 | getUserReservationsByPaymentStatus | 사용자별 + 결제 상태별 예약 목록 조회 | 특정 사용자의 특정 결제 상태 예약 목록을 조회한다 |
| 예약 | getActiveReservations | 이용중인 수강권/강의내역 조회 | 이용중인 수강권/강의내역을 조회한다 (결제완료 상태만 반환) |
| 예약 | makeReservation | 예약 생성 | 새로운 예약을 생성한다 |
| 예약 | updatePaymentStatus | 결제 상태 변경 | 예약의 결제 상태를 변경한다 |
| 예약 | deleteReservation | 예약 삭제 | 예약을 삭제한다 (소프트 삭제) |
| 리뷰 | getAllReviews | 전체 리뷰 목록 조회 | 전체 리뷰 목록을 조회한다 |
| 리뷰 | getReview | 리뷰 단건 조회 | 리뷰 ID로 리뷰 정보를 조회한다 |
| 리뷰 | getByInstructor | 강사별 리뷰 조회 | 특정 강사의 리뷰 목록을 조회한다 |
| 리뷰 | getByUser | 사용자별 리뷰 조회 | 특정 사용자의 리뷰 목록을 조회한다 |
| 리뷰 | getByReservation | 예약별 리뷰 조회 | 특정 예약의 리뷰를 조회한다 |
| 리뷰 | createReview | 리뷰 생성 | 새로운 리뷰를 생성한다 |
| 리뷰 | updateReview | 리뷰 수정 | 리뷰 정보를 수정한다 |
| 리뷰 | deleteReview | 리뷰 삭제 | 리뷰를 삭제한다 |
| 거래 신청 | getAll | 전체 거래 신청 목록 조회 | 전체 이용권 거래 신청 목록을 조회한다 |
| 거래 신청 | getById | 거래 신청 단건 조회 | 거래 ID로 거래 신청 정보를 조회한다 |
| 거래 신청 | getByUser | 사용자별 거래 신청 조회 | 특정 사용자의 거래 신청 목록을 조회한다 |
| 거래 신청 | getByReservation | 예약별 거래 신청 조회 | 특정 예약의 거래 신청 목록을 조회한다 |
| 거래 신청 | createTradeRequest | 이용권 거래 신청 생성 | 새로운 이용권 거래 신청을 생성한다 |
| 거래 신청 | updateStatus | 거래 상태 변경 | 거래 신청의 상태를 변경한다 |
| 이용내역 | getByUser | 사용자별 이용내역 조회 | 특정 사용자의 이용내역을 조회한다 |
| 이용내역 | getByReservation | 예약별 이용내역 조회 | 특정 예약의 이용내역을 조회한다 |
| 이용내역 | getByTrade | 거래별 이용내역 조회 | 특정 거래의 이용내역을 조회한다 |

## 📋 API 명세서

---

### 전체 예약 목록 조회

#### API 정보
- **URL**: `/api/reservations`
- **Method**: `GET`
- **Description**: 전체 예약 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| - | - | Array[Reservation] | Y | 예약 목록 | - |
| | rsv_id | Long | Y | 예약 ID | - |
| | usr_id | String | Y | 회원(사용자) ID | - |
| | schd_id | Long | Y | 스케줄 ID | - |
| | tkt_id | Long | N | 사용이용권 ID | - |
| | stts_cd | String | Y | 상태코드 | - |
| | reg_dt | LocalDateTime | Y | 등록일시 | - |
| | cncl_rsn | String | N | 취소/변경사유(관리자용) | - |
| | mod_usr_ID | String | N | 수정자 ID | - |

#### Request Example
```
GET /api/reservations
```

#### Response Example
```json
[
  {
    "rsv_id": 1,
    "usr_id": "user123",
    "schd_id": 10,
    "tkt_id": 5,
    "stts_cd": "예약완료",
    "reg_dt": "2024-01-15T10:30:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  }
]
```

---

### 예약 단건 조회

#### API 정보
- **URL**: `/api/reservations/{id}`
- **Method**: `GET`
- **Description**: 예약 ID로 예약 정보를 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Long | Y | 예약 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| - | - | Reservation | Y | 예약 정보 | - |
| | rsv_id | Long | Y | 예약 ID | - |
| | usr_id | String | Y | 회원(사용자) ID | - |
| | schd_id | Long | Y | 스케줄 ID | - |
| | tkt_id | Long | N | 사용이용권 ID | - |
| | stts_cd | String | Y | 상태코드 | - |
| | reg_dt | LocalDateTime | Y | 등록일시 | - |
| | cncl_rsn | String | N | 취소/변경사유(관리자용) | - |
| | mod_usr_ID | String | N | 수정자 ID | - |

#### Request Example
```
GET /api/reservations/1
```

#### Response Example
```json
{
  "rsv_id": 1,
  "usr_id": "user123",
  "schd_id": 10,
  "tkt_id": 5,
  "stts_cd": "예약완료",
  "reg_dt": "2024-01-15T10:30:00",
  "cncl_rsn": null,
  "mod_usr_ID": null
}
```

---

### 사용자별 예약 목록 조회

#### API 정보
- **URL**: `/api/reservations/user/{userId}`
- **Method**: `GET`
- **Description**: 특정 사용자의 예약 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | userId | Long | Y | 사용자 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| - | - | Array[Reservation] | Y | 예약 목록 | - |
| | rsv_id | Long | Y | 예약 ID | - |
| | usr_id | String | Y | 회원(사용자) ID | - |
| | schd_id | Long | Y | 스케줄 ID | - |
| | tkt_id | Long | N | 사용이용권 ID | - |
| | stts_cd | String | Y | 상태코드 | - |
| | reg_dt | LocalDateTime | Y | 등록일시 | - |
| | cncl_rsn | String | N | 취소/변경사유(관리자용) | - |
| | mod_usr_ID | String | N | 수정자 ID | - |

#### Request Example
```
GET /api/reservations/user/1
```

#### Response Example
```json
[
  {
    "rsv_id": 1,
    "usr_id": "user123",
    "schd_id": 10,
    "tkt_id": 5,
    "stts_cd": "예약완료",
    "reg_dt": "2024-01-15T10:30:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  }
]
```

---

### 결제 상태별 예약 목록 조회

#### API 정보
- **URL**: `/api/reservations/payment-status/{paymentStatus}`
- **Method**: `GET`
- **Description**: 결제 상태별로 예약 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | paymentStatus | String | Y | 결제 상태 (대기중/결제완료) | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| - | - | Array[Reservation] | Y | 예약 목록 | - |
| | rsv_id | Long | Y | 예약 ID | - |
| | usr_id | String | Y | 회원(사용자) ID | - |
| | schd_id | Long | Y | 스케줄 ID | - |
| | tkt_id | Long | N | 사용이용권 ID | - |
| | stts_cd | String | Y | 상태코드 | - |
| | reg_dt | LocalDateTime | Y | 등록일시 | - |
| | cncl_rsn | String | N | 취소/변경사유(관리자용) | - |
| | mod_usr_ID | String | N | 수정자 ID | - |

#### Request Example
```
GET /api/reservations/payment-status/대기중
```

#### Response Example
```json
[
  {
    "rsv_id": 1,
    "usr_id": "user123",
    "schd_id": 10,
    "tkt_id": 5,
    "stts_cd": "예약완료",
    "reg_dt": "2024-01-15T10:30:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  }
]
```

---

### 사용자별 + 결제 상태별 예약 목록 조회

#### API 정보
- **URL**: `/api/reservations/user/{userId}/payment-status/{paymentStatus}`
- **Method**: `GET`
- **Description**: 특정 사용자의 특정 결제 상태 예약 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | userId | Long | Y | 사용자 ID | |
| Path Variable | paymentStatus | String | Y | 결제 상태 (대기중/결제완료) | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| - | - | Array[Reservation] | Y | 예약 목록 | - |
| | rsv_id | Long | Y | 예약 ID | - |
| | usr_id | String | Y | 회원(사용자) ID | - |
| | schd_id | Long | Y | 스케줄 ID | - |
| | tkt_id | Long | N | 사용이용권 ID | - |
| | stts_cd | String | Y | 상태코드 | - |
| | reg_dt | LocalDateTime | Y | 등록일시 | - |
| | cncl_rsn | String | N | 취소/변경사유(관리자용) | - |
| | mod_usr_ID | String | N | 수정자 ID | - |

#### Request Example
```
GET /api/reservations/user/1/payment-status/결제완료
```

#### Response Example
```json
[
  {
    "rsv_id": 2,
    "usr_id": "user123",
    "schd_id": 11,
    "tkt_id": 6,
    "stts_cd": "결제완료",
    "reg_dt": "2024-01-14T14:20:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  }
]
```

---

### 이용중인 수강권/강의내역 조회

#### API 정보
- **URL**: `/api/reservations/user/{userId}/active`
- **Method**: `GET`
- **Description**: 이용중인 수강권/강의내역을 조회한다 (결제완료 상태만 반환)

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | userId | Long | Y | 사용자 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[Reservation] | Y | 예약 목록 (결제완료만) | |
| | id | Long | Y | 예약 ID | |
| | userId | Long | Y | 사용자 ID | |
| | classId | Long | N | 클래스 ID | |
| | facilityId | Long | N | 시설 ID | |
| | paymentStatus | String | Y | 결제 상태 (결제완료) | 결제완료 |
| | deleted | Boolean | Y | 삭제 여부 | false |
| | createdAt | LocalDateTime | Y | 생성 시간 | |
| | deletedAt | LocalDateTime | N | 삭제 시간 | |

#### Request Example
```
GET /api/reservations/user/1/active
```

#### Response Example
```json
[
  {
    "rsv_id": 2,
    "usr_id": "user123",
    "schd_id": 11,
    "tkt_id": 6,
    "stts_cd": "결제완료",
    "reg_dt": "2024-01-14T14:20:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  }
]
```

---

### 예약 생성

#### API 정보
- **URL**: `/api/reservations`
- **Method**: `POST`
- **Description**: 새로운 예약을 생성한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Content-Type | Content-Type | String | Y | application/json | |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Body | - | Reservation | Y | 예약 정보 | - |
| | usr_id | String | Y | 회원(사용자) ID | - |
| | schd_id | Long | Y | 스케줄 ID | - |
| | tkt_id | Long | N | 사용이용권 ID | - |
| | stts_cd | String | Y | 상태코드 | - |
| | cncl_rsn | String | N | 취소/변경사유(관리자용) | - |
| | mod_usr_ID | String | N | 수정자 ID | - |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| String | | String | Y | 응답 메시지 | |

#### Request Example
```
POST /api/reservations
Content-Type: application/json

{
  "usr_id": "user123",
  "schd_id": 10,
  "tkt_id": 5,
  "stts_cd": "예약완료"
}
```

#### Response Example
```
예약 완료
```

---

### 결제 상태 변경

#### API 정보
- **URL**: `/api/reservations/{id}/payment-status`
- **Method**: `PATCH`
- **Description**: 예약의 결제 상태를 변경한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Query Parameter | paymentStatus | String | Y | 결제 상태 (대기중/결제완료) | |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Long | Y | 예약 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| String | | String | Y | 응답 메시지 | |

#### Request Example
```
PATCH /api/reservations/1/payment-status?paymentStatus=결제완료
```

#### Response Example
```
결제 상태가 변경되었습니다: 결제완료
```

---

### 예약 삭제

#### API 정보
- **URL**: `/api/reservations/{id}`
- **Method**: `DELETE`
- **Description**: 예약을 삭제한다 (소프트 삭제)

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Long | Y | 예약 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| String | | String | Y | 응답 메시지 | |

#### Request Example
```
DELETE /api/reservations/1
```

#### Response Example
```
예약 삭제 완료
```

---

### 전체 리뷰 목록 조회

#### API 정보
- **URL**: `/review`
- **Method**: `GET`
- **Description**: 전체 리뷰 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[Review] | Y | 리뷰 목록 | |
| | id | Integer | Y | 리뷰 ID | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |
| | createdAt | LocalDateTime | Y | 생성 시간 | |

#### Request Example
```
GET /review
```

#### Response Example
```json
[
  {
    "id": 1,
    "userId": 1,
    "instructorId": 5,
    "reservationId": 10,
    "rating": 5,
    "comment": "좋은 강의였습니다.",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### 리뷰 단건 조회

#### API 정보
- **URL**: `/review/{id}`
- **Method**: `GET`
- **Description**: 리뷰 ID로 리뷰 정보를 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Integer | Y | 리뷰 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Object | | Review | Y | 리뷰 정보 | |
| | id | Integer | Y | 리뷰 ID | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |
| | createdAt | LocalDateTime | Y | 생성 시간 | |

#### Request Example
```
GET /review/1
```

#### Response Example
```json
{
  "id": 1,
  "userId": 1,
  "instructorId": 5,
  "reservationId": 10,
  "rating": 5,
  "comment": "좋은 강의였습니다.",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### 강사별 리뷰 조회

#### API 정보
- **URL**: `/review/instructor/{instructorId}`
- **Method**: `GET`
- **Description**: 특정 강사의 리뷰 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | instructorId | Integer | Y | 강사 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[Review] | Y | 리뷰 목록 | |
| | id | Integer | Y | 리뷰 ID | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |
| | createdAt | LocalDateTime | Y | 생성 시간 | |

#### Request Example
```
GET /review/instructor/5
```

#### Response Example
```json
[
  {
    "id": 1,
    "userId": 1,
    "instructorId": 5,
    "reservationId": 10,
    "rating": 5,
    "comment": "좋은 강의였습니다.",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### 사용자별 리뷰 조회

#### API 정보
- **URL**: `/review/user/{userId}`
- **Method**: `GET`
- **Description**: 특정 사용자의 리뷰 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | userId | Integer | Y | 사용자 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[Review] | Y | 리뷰 목록 | |
| | id | Integer | Y | 리뷰 ID | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |
| | createdAt | LocalDateTime | Y | 생성 시간 | |

#### Request Example
```
GET /review/user/1
```

#### Response Example
```json
[
  {
    "id": 1,
    "userId": 1,
    "instructorId": 5,
    "reservationId": 10,
    "rating": 5,
    "comment": "좋은 강의였습니다.",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### 예약별 리뷰 조회

#### API 정보
- **URL**: `/review/reservation/{reservationId}`
- **Method**: `GET`
- **Description**: 특정 예약의 리뷰를 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | reservationId | Integer | Y | 예약 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Object | | Review | Y | 리뷰 정보 | |
| | id | Integer | Y | 리뷰 ID | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |
| | createdAt | LocalDateTime | Y | 생성 시간 | |

#### Request Example
```
GET /review/reservation/10
```

#### Response Example
```json
{
  "id": 1,
  "userId": 1,
  "instructorId": 5,
  "reservationId": 10,
  "rating": 5,
  "comment": "좋은 강의였습니다.",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

### 리뷰 생성

#### API 정보
- **URL**: `/review`
- **Method**: `POST`
- **Description**: 새로운 리뷰를 생성한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Content-Type | Content-Type | String | Y | application/json | |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Object | | Review | Y | 리뷰 정보 | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Integer | | Integer | Y | 생성된 리뷰 ID | |

#### Request Example
```
POST /review
Content-Type: application/json

{
  "userId": 1,
  "instructorId": 5,
  "reservationId": 10,
  "rating": 5,
  "comment": "좋은 강의였습니다."
}
```

#### Response Example
```
1
```

---

### 리뷰 수정

#### API 정보
- **URL**: `/review/{id}`
- **Method**: `PUT`
- **Description**: 리뷰 정보를 수정한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Content-Type | Content-Type | String | Y | application/json | |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Integer | Y | 리뷰 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Object | | Review | Y | 수정할 리뷰 정보 | |
| | userId | Integer | Y | 사용자 ID | |
| | instructorId | Integer | Y | 강사 ID | |
| | reservationId | Integer | Y | 예약 ID | |
| | rating | Integer | Y | 평점 | |
| | comment | String | N | 리뷰 내용 | |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Integer | | Integer | Y | 수정된 행 수 | |

#### Request Example
```
PUT /review/1
Content-Type: application/json

{
  "userId": 1,
  "instructorId": 5,
  "reservationId": 10,
  "rating": 4,
  "comment": "수정된 리뷰 내용"
}
```

#### Response Example
```
1
```

---

### 리뷰 삭제

#### API 정보
- **URL**: `/review/{id}`
- **Method**: `DELETE`
- **Description**: 리뷰를 삭제한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Integer | Y | 리뷰 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Integer | | Integer | Y | 삭제된 행 수 | |

#### Request Example
```
DELETE /review/1
```

#### Response Example
```
1
```

---

### 전체 거래 신청 목록 조회

#### API 정보
- **URL**: `/api/trades`
- **Method**: `GET`
- **Description**: 전체 이용권 거래 신청 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[TradeRequest] | Y | 거래 신청 목록 | |
| | id | Long | Y | 거래 ID | |
| | reservationId | Long | Y | 예약 ID | |
| | sellerUserId | Long | Y | 판매자 사용자 ID | |
| | buyerUserId | Long | N | 구매자 사용자 ID | |
| | price | Integer | N | 거래 금액 | |
| | status | String | Y | 거래 상태 (PENDING/APPROVED/REJECTED) | PENDING |
| | createdAt | LocalDateTime | Y | 생성 시간 | |
| | updatedAt | LocalDateTime | Y | 수정 시간 | |

#### Request Example
```
GET /api/trades
```

#### Response Example
```json
[
  {
    "id": 10,
    "reservationId": 2,
    "sellerUserId": 1,
    "buyerUserId": 15,
    "price": 50000,
    "status": "PENDING",
    "createdAt": "2024-01-20T12:00:00",
    "updatedAt": "2024-01-20T12:00:00"
  }
]
```

---

### 거래 신청 단건 조회

#### API 정보
- **URL**: `/api/trades/{id}`
- **Method**: `GET`
- **Description**: 거래 ID로 거래 신청 정보를 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Long | Y | 거래 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Object | | TradeRequest | Y | 거래 신청 정보 | |
| | id | Long | Y | 거래 ID | |
| | reservationId | Long | Y | 예약 ID | |
| | sellerUserId | Long | Y | 판매자 사용자 ID | |
| | buyerUserId | Long | N | 구매자 사용자 ID | |
| | price | Integer | N | 거래 금액 | |
| | status | String | Y | 거래 상태 (PENDING/APPROVED/REJECTED) | PENDING |
| | createdAt | LocalDateTime | Y | 생성 시간 | |
| | updatedAt | LocalDateTime | Y | 수정 시간 | |

#### Request Example
```
GET /api/trades/10
```

#### Response Example
```json
{
  "id": 10,
  "reservationId": 2,
  "sellerUserId": 1,
  "buyerUserId": 15,
  "price": 50000,
  "status": "PENDING",
  "createdAt": "2024-01-20T12:00:00",
  "updatedAt": "2024-01-20T12:00:00"
}
```

---

### 사용자별 거래 신청 조회

#### API 정보
- **URL**: `/api/trades/user/{userId}`
- **Method**: `GET`
- **Description**: 특정 사용자의 거래 신청 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | userId | Long | Y | 사용자 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[TradeRequest] | Y | 거래 신청 목록 | |
| | id | Long | Y | 거래 ID | |
| | reservationId | Long | Y | 예약 ID | |
| | sellerUserId | Long | Y | 판매자 사용자 ID | |
| | buyerUserId | Long | N | 구매자 사용자 ID | |
| | price | Integer | N | 거래 금액 | |
| | status | String | Y | 거래 상태 (PENDING/APPROVED/REJECTED) | PENDING |
| | createdAt | LocalDateTime | Y | 생성 시간 | |
| | updatedAt | LocalDateTime | Y | 수정 시간 | |

#### Request Example
```
GET /api/trades/user/1
```

#### Response Example
```json
[
  {
    "id": 10,
    "reservationId": 2,
    "sellerUserId": 1,
    "buyerUserId": 15,
    "price": 50000,
    "status": "PENDING",
    "createdAt": "2024-01-20T12:00:00",
    "updatedAt": "2024-01-20T12:00:00"
  }
]
```

---

### 예약별 거래 신청 조회

#### API 정보
- **URL**: `/api/trades/reservation/{reservationId}`
- **Method**: `GET`
- **Description**: 특정 예약의 거래 신청 목록을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | reservationId | Long | Y | 예약 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[TradeRequest] | Y | 거래 신청 목록 | |
| | id | Long | Y | 거래 ID | |
| | reservationId | Long | Y | 예약 ID | |
| | sellerUserId | Long | Y | 판매자 사용자 ID | |
| | buyerUserId | Long | N | 구매자 사용자 ID | |
| | price | Integer | N | 거래 금액 | |
| | status | String | Y | 거래 상태 (PENDING/APPROVED/REJECTED) | PENDING |
| | createdAt | LocalDateTime | Y | 생성 시간 | |
| | updatedAt | LocalDateTime | Y | 수정 시간 | |

#### Request Example
```
GET /api/trades/reservation/2
```

#### Response Example
```json
[
  {
    "id": 10,
    "reservationId": 2,
    "sellerUserId": 1,
    "buyerUserId": 15,
    "price": 50000,
    "status": "PENDING",
    "createdAt": "2024-01-20T12:00:00",
    "updatedAt": "2024-01-20T12:00:00"
  }
]
```

---

### 이용권 거래 신청 생성

#### API 정보
- **URL**: `/api/trades`
- **Method**: `POST`
- **Description**: 새로운 이용권 거래 신청을 생성한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Content-Type | Content-Type | String | Y | application/json | |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Object | | TradeRequest | Y | 거래 신청 정보 | |
| | reservationId | Long | Y | 예약 ID | |
| | sellerUserId | Long | Y | 판매자 사용자 ID | |
| | buyerUserId | Long | N | 구매자 사용자 ID | |
| | price | Integer | N | 거래 금액 | |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| String | | String | Y | 응답 메시지 | |

#### Request Example
```
POST /api/trades
Content-Type: application/json

{
  "reservationId": 2,
  "sellerUserId": 1,
  "buyerUserId": 15,
  "price": 50000
}
```

#### Response Example
```
거래 신청이 등록되었습니다.
```

---

### 거래 상태 변경

#### API 정보
- **URL**: `/api/trades/{id}/status`
- **Method**: `PATCH`
- **Description**: 거래 신청의 상태를 변경한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Query Parameter | status | String | Y | 거래 상태 (PENDING/APPROVED/REJECTED) | |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | id | Long | Y | 거래 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| String | | String | Y | 응답 메시지 | |

#### Request Example
```
PATCH /api/trades/10/status?status=APPROVED
```

#### Response Example
```
거래 상태가 변경되었습니다: APPROVED
```

---

### 사용자별 이용내역 조회

#### API 정보
- **URL**: `/api/history/user/{userId}`
- **Method**: `GET`
- **Description**: 특정 사용자의 이용내역을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | userId | Long | Y | 사용자 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[HistoryEntry] | Y | 이용내역 목록 | |
| | id | Long | Y | 히스토리 ID | |
| | userId | Long | Y | 사용자 ID | |
| | reservationId | Long | N | 예약 ID | |
| | tradeId | Long | N | 거래 ID | |
| | action | String | Y | 이벤트명 (예: TRADE_CREATED, TRADE_STATUS_APPROVED) | |
| | detail | String | N | 추가 설명 | |
| | createdAt | LocalDateTime | Y | 기록 시각 | |

#### Request Example
```
GET /api/history/user/1
```

#### Response Example
```json
[
  {
    "id": 1,
    "userId": 1,
    "reservationId": 2,
    "tradeId": 10,
    "action": "TRADE_CREATED",
    "detail": "거래 신청이 생성되었습니다.",
    "createdAt": "2024-01-20T12:00:00"
  }
]
```

---

### 예약별 이용내역 조회

#### API 정보
- **URL**: `/api/history/reservation/{reservationId}`
- **Method**: `GET`
- **Description**: 특정 예약의 이용내역을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | reservationId | Long | Y | 예약 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[HistoryEntry] | Y | 이용내역 목록 | |
| | id | Long | Y | 히스토리 ID | |
| | userId | Long | Y | 사용자 ID | |
| | reservationId | Long | N | 예약 ID | |
| | tradeId | Long | N | 거래 ID | |
| | action | String | Y | 이벤트명 (예: TRADE_CREATED, TRADE_STATUS_APPROVED) | |
| | detail | String | N | 추가 설명 | |
| | createdAt | LocalDateTime | Y | 기록 시각 | |

#### Request Example
```
GET /api/history/reservation/2
```

#### Response Example
```json
[
  {
    "id": 1,
    "userId": 1,
    "reservationId": 2,
    "tradeId": 10,
    "action": "TRADE_CREATED",
    "detail": "거래 신청이 생성되었습니다.",
    "createdAt": "2024-01-20T12:00:00"
  }
]
```

---

### 거래별 이용내역 조회

#### API 정보
- **URL**: `/api/history/trade/{tradeId}`
- **Method**: `GET`
- **Description**: 특정 거래의 이용내역을 조회한다

#### Request

##### Header
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Query Params
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

##### Path Variables
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Path Variable | tradeId | Long | Y | 거래 ID | |

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
|            |      |      |           |             |         |

#### Response

##### Body
| Parameters | Name | Type | Mandatory | Description | Default |
|------------|------|------|-----------|-------------|---------|
| Array | | Array[HistoryEntry] | Y | 이용내역 목록 | |
| | id | Long | Y | 히스토리 ID | |
| | userId | Long | Y | 사용자 ID | |
| | reservationId | Long | N | 예약 ID | |
| | tradeId | Long | N | 거래 ID | |
| | action | String | Y | 이벤트명 (예: TRADE_CREATED, TRADE_STATUS_APPROVED) | |
| | detail | String | N | 추가 설명 | |
| | createdAt | LocalDateTime | Y | 기록 시각 | |

#### Request Example
```
GET /api/history/trade/10
```

#### Response Example
```json
[
  {
    "id": 1,
    "userId": 1,
    "reservationId": 2,
    "tradeId": 10,
    "action": "TRADE_CREATED",
    "detail": "거래 신청이 생성되었습니다.",
    "createdAt": "2024-01-20T12:00:00"
  }
]
```

---

### 예약 관련 API

| 메서드 | 엔드포인트 | 설명 | 요청 본문 |
|--------|-----------|------|----------|
| GET | `/api/reservations` | 전체 예약 목록 조회 | |
| GET | `/api/reservations/{id}` | 예약 단건 조회 | |
| GET | `/api/reservations/user/{userId}` | 사용자별 예약 목록 조회 | |
| GET | `/api/reservations/user/{userId}/active` | **이용중인 수강권/강의내역 조회** (결제완료 상태만) | |
| GET | `/api/reservations/payment-status/{paymentStatus}` | 결제 상태별 예약 목록 조회 | |
| GET | `/api/reservations/user/{userId}/payment-status/{paymentStatus}` | 사용자별 + 결제 상태별 예약 목록 조회 | |
| GET | `/api/trades` | 이용권 거래 신청 전체 조회 | |
| GET | `/api/trades/{id}` | 이용권 거래 신청 단건 조회 | |
| GET | `/api/trades/user/{userId}` | 사용자별 이용권 거래 신청 조회 | |
| GET | `/api/trades/reservation/{reservationId}` | 예약별 이용권 거래 신청 조회 | |
| POST | `/api/trades` | 이용권 거래 신청 생성 | `TradeRequest` 객체 |
| PATCH | `/api/trades/{id}/status` | 이용권 거래 상태 변경 | `status` 파라미터 (PENDING/APPROVED/REJECTED) |
| POST | `/api/reservations` | 예약 생성 | `Reservation` 객체 |
| PATCH | `/api/reservations/{id}/payment-status` | 결제 상태 변경 | `paymentStatus` 파라미터 |
| DELETE | `/api/reservations/{id}` | 예약 삭제 | |
| GET | `/api/history/user/{userId}` | 사용자별 이용내역 조회 | |
| GET | `/api/history/reservation/{reservationId}` | 예약별 이용내역 조회 | |
| GET | `/api/history/trade/{tradeId}` | 거래별 이용내역 조회 | |

### 예시 요청/응답

#### 1. 사용자별 예약 목록 조회
```http
GET /api/reservations/user/1
```

**응답:**
```json
[
  {
    "rsv_id": 1,
    "usr_id": "user123",
    "schd_id": 10,
    "tkt_id": 5,
    "stts_cd": "예약완료",
    "reg_dt": "2024-01-15T10:30:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  },
  {
    "rsv_id": 2,
    "usr_id": "user123",
    "schd_id": 11,
    "tkt_id": 6,
    "stts_cd": "결제완료",
    "reg_dt": "2024-01-14T14:20:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  }
]
```

#### 2. 이용중인 수강권/강의내역 조회 (결제완료 상태만)
```http
GET /api/reservations/user/1/active
```

**응답:**
```json
[
  {
    "rsv_id": 2,
    "usr_id": "user123",
    "schd_id": 11,
    "tkt_id": 6,
    "stts_cd": "결제완료",
    "reg_dt": "2024-01-14T14:20:00",
    "cncl_rsn": null,
    "mod_usr_ID": null
  },
  {
    "id": 3,
    "userId": 1,
    "classId": 12,
    "facilityId": 7,
    "paymentStatus": "결제완료",
    "deleted": false,
    "createdAt": "2024-01-13T09:15:00",
    "deletedAt": null
  }
]
```

> **참고**: 이 API는 결제완료 상태인 예약만 반환하므로, 이용중인 수강권/강의내역 페이지에서 사용하기에 적합합니다.

#### 3. 이용권 거래 신청 생성
```http
POST /api/trades
```

**요청 바디 예시:**
```json
{
  "reservationId": 2,
  "sellerUserId": 1,
  "buyerUserId": 15,
  "price": 50000
}
```

**성공 응답:**
```json
"거래 신청이 등록되었습니다."
```

#### 4. 이용권 거래 신청 조회 (사용자별)
```http
GET /api/trades/user/1
```

**응답 예시:**
```json
[
  {
    "id": 10,
    "reservationId": 2,
    "sellerUserId": 1,
    "buyerUserId": 15,
    "price": 50000,
    "status": "PENDING",
    "createdAt": "2024-01-20T12:00:00",
    "updatedAt": "2024-01-20T12:00:00"
  }
]
```

#### 5. 이용권 거래 상태 변경
```http
PATCH /api/trades/10/status?status=APPROVED
```

**성공 응답:**
```json
"거래 상태가 변경되었습니다: APPROVED"
```

#### 6. 결제 상태별 예약 목록 조회
```http
GET /api/reservations/payment-status/대기중
```

**응답:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "classId": 10,
    "facilityId": 5,
    "paymentStatus": "대기중",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

#### 4. 사용자별 + 결제 상태별 예약 목록 조회
```http
GET /api/reservations/user/1/payment-status/결제완료
```

**응답:**
```json
[
  {
    "id": 2,
    "userId": 1,
    "classId": 11,
    "facilityId": 6,
    "paymentStatus": "결제완료",
    "createdAt": "2024-01-14T14:20:00"
  }
]
```

#### 5. 결제 상태 변경
```http
PATCH /api/reservations/1/payment-status?paymentStatus=결제완료
```

**성공 응답:**
```json
"결제 상태가 변경되었습니다: 결제완료"
```

**실패 응답 (잘못된 상태값):**
```json
"결제 상태는 '대기중' 또는 '결제완료'만 가능합니다."
```

#### 6. 예약 삭제
```http
DELETE /api/reservations/1
```

**성공 응답:**
```json
"예약 삭제 완료"
```

**실패 응답:**
```
404 Not Found
```

## 🗄 데이터베이스 구조

### Reservation 테이블

```sql
CREATE TABLE reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    class_id BIGINT,
    facility_id BIGINT,
    payment_status VARCHAR(20) DEFAULT '대기중',
    deleted BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL
);
```

### 컬럼 설명

- `reservation_id`: 예약 고유 ID (Primary Key)
- `user_id`: 사용자 ID (Foreign Key)
- `class_id`: 클래스 ID
- `facility_id`: 시설 ID
- `payment_status`: 결제 현황 ("대기중", "결제완료") - 기본값: "대기중"
- `deleted`: 삭제 여부 (false: 활성, true: 삭제됨) - 기본값: false
- `created_at`: 예약 생성 시간
- `deleted_at`: 삭제 시간 (소프트 삭제 시 기록됨)

## ⚙️ 설정 파일

### application.properties 주요 설정

```properties
# 데이터베이스 설정
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/testdb
spring.datasource.username=root
spring.datasource.password=1234

# MyBatis 설정
mybatis.mapper-locations=classpath:/mapper/**/*.xml
mybatis.configuration.map-underscore-to-camel-case=true
```

### MyBatis 설정 설명

- `mapper-locations`: MyBatis XML 매퍼 파일 위치 지정
- `map-underscore-to-camel-case`: 스네이크 케이스 컬럼명을 카멜 케이스로 자동 변환
  - 예: `user_id` → `userId`, `created_at` → `createdAt`

## 🔧 실행 방법

### 1. 사전 요구사항
- Java 21 이상
- MariaDB 설치 및 실행
- 데이터베이스 생성 및 테이블 생성

### 2. 데이터베이스 설정
```sql
CREATE DATABASE testdb;
USE testdb;

CREATE TABLE reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    class_id BIGINT,
    facility_id BIGINT,
    payment_status VARCHAR(20) DEFAULT '대기중',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**기존 테이블에 컬럼 추가 (이미 테이블이 있는 경우):**
```sql
-- 결제 상태 컬럼 추가
ALTER TABLE reservation 
ADD COLUMN payment_status VARCHAR(20) DEFAULT '대기중';

-- 소프트 삭제 관련 컬럼 추가
ALTER TABLE reservation 
ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE reservation 
ADD COLUMN deleted_at DATETIME NULL;
```

### TradeRequest 테이블

```sql
CREATE TABLE trade_request (
    trade_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    seller_user_id BIGINT NOT NULL,
    buyer_user_id BIGINT,
    price INT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING / APPROVED / REJECTED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### TradeRequest 컬럼 설명

- `trade_id`: 거래 신청 고유 ID (PK)
- `reservation_id`: 거래 대상 예약 ID (FK)
- `seller_user_id`: 판매자 사용자 ID
- `buyer_user_id`: 구매자 사용자 ID (선택)
- `price`: 거래 희망 금액
- `status`: 거래 상태 (PENDING/APPROVED/REJECTED)
- `created_at`: 생성 시간
- `updated_at`: 수정 시간

### History 테이블

```sql
CREATE TABLE history (
    history_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    reservation_id BIGINT,
    trade_id BIGINT,
    action VARCHAR(50),      -- 예: TRADE_CREATED, TRADE_STATUS_APPROVED
    detail VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### History 컬럼 설명

- `history_id`: 히스토리 고유 ID (PK)
- `user_id`: 작업을 수행한 사용자 ID
- `reservation_id`: 관련 예약 ID
- `trade_id`: 관련 거래 ID
- `action`: 이벤트명 (예: TRADE_CREATED, TRADE_STATUS_APPROVED/REJECTED)
- `detail`: 추가 설명
- `created_at`: 기록 시각

### 3. 애플리케이션 실행
```bash
# Gradle을 사용한 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/myPage-0.0.1-SNAPSHOT.jar
```

### 4. API 테스트
```bash
# 전체 예약 목록 조회
curl http://localhost:8080/api/reservations

# 사용자별 예약 목록 조회
curl http://localhost:8080/api/reservations/user/1

# 결제 상태별 예약 목록 조회
curl http://localhost:8080/api/reservations/payment-status/대기중
curl http://localhost:8080/api/reservations/payment-status/결제완료

# 사용자별 + 결제 상태별 예약 목록 조회
curl http://localhost:8080/api/reservations/user/1/payment-status/결제완료

# 결제 상태 변경
curl -X PATCH "http://localhost:8080/api/reservations/1/payment-status?paymentStatus=결제완료"

# 예약 삭제
curl -X DELETE http://localhost:8080/api/reservations/1
```

## 🔍 주요 기술 특징

### 1. MyBatis ResultMap
- 데이터베이스 컬럼과 Java 객체 필드 간 매핑 정의
- 스네이크 케이스와 카멜 케이스 자동 변환 지원

### 2. Lombok 활용
- `@Data`: Getter/Setter/ToString 등 자동 생성
- `@RequiredArgsConstructor`: final 필드에 대한 생성자 자동 생성

### 3. Spring 의존성 주입
- `@Autowired`: 필드 주입
- `@RequiredArgsConstructor`: 생성자 주입 (권장)

### 4. RESTful API 설계
- HTTP 메서드 활용 (GET, POST, PATCH, DELETE)
- 리소스 기반 URL 구조
- 적절한 HTTP 상태 코드 반환

### 5. 결제 현황 관리
- 예약 생성 시 기본값으로 "대기중" 설정
- 결제 상태별 필터링 조회 지원
- 결제 상태 변경 API 제공
- 결제 상태 유효성 검증 ("대기중", "결제완료"만 허용)

### 6. 소프트 삭제 (Soft Delete)
- 물리적 삭제 대신 논리적 삭제 방식 사용
- `deleted` 플래그로 삭제 상태 관리
- 삭제 시간(`deleted_at`) 기록으로 이력 추적 가능
- 모든 조회 쿼리에서 삭제된 데이터 자동 필터링
- 데이터 복구 및 감사 목적에 유용

## 💳 결제 현황 관리 기능

### 결제 상태 값
- `대기중`: 예약은 완료되었으나 결제가 아직 완료되지 않은 상태
- `결제완료`: 결제가 완료된 예약

### 결제 상태 동작 원리

1. **예약 생성 시**
   - `paymentStatus`가 지정되지 않으면 자동으로 "대기중"으로 설정
   - 명시적으로 "결제완료"로 지정 가능
   - `ReservationService.reserve()` 메서드에서 기본값 처리

2. **결제 상태 조회**
   - 전체 예약 중 특정 결제 상태만 필터링하여 조회 가능
   - 사용자별 예약 중 결제 상태로 추가 필터링 가능
   - SQL의 `WHERE` 절을 사용하여 효율적인 필터링

3. **결제 상태 변경**
   - `PATCH /api/reservations/{id}/payment-status` 엔드포인트 사용
   - "대기중" ↔ "결제완료" 상태 변경 가능
   - 잘못된 상태값 입력 시 유효성 검증으로 에러 반환
   - 부분 업데이트(Partial Update) 방식으로 `payment_status` 컬럼만 변경

### 결제 상태 관리 시나리오

#### 시나리오 1: 예약 생성부터 결제 완료까지
```
1. 사용자가 예약 생성
   POST /api/reservations
   → paymentStatus: "대기중" (기본값)

2. 결제 시스템에서 결제 완료 처리
   PATCH /api/reservations/1/payment-status?paymentStatus=결제완료
   → paymentStatus: "결제완료"

3. 결제 완료된 예약만 조회
   GET /api/reservations/payment-status/결제완료
```

### 이용권 거래 신청 동작 원리
```
사용자 요청
    ↓
POST /api/trades (거래 신청 생성)
    ↓
TradeRequestController.createTradeRequest()
    ↓
TradeRequestService.create() - 기본 상태 PENDING 설정
    ↓
TradeRequestMapper.insert() - trade_request 테이블 INSERT
    ↓
DB 저장 후 결과 반환

거래 현황 조회
    ↓
GET /api/trades/user/{userId}
    ↓
TradeRequestController.getByUser()
    ↓
TradeRequestService.getByUser()
    ↓
TradeRequestMapper.findByUser()
    ↓
DB 조회 후 결과 반환

거래 상태 변경
    ↓
PATCH /api/trades/{id}/status?status=APPROVED|REJECTED
    ↓
TradeRequestController.updateStatus()
    ↓
TradeRequestService.updateStatus()
    ↓
TradeRequestMapper.updateStatus()
    ↓
DB 업데이트 후 결과 반환
```

### 이용내역(History) 기록 동작 원리
```
거래 신청 생성
    ↓
TradeRequestService.create()에서 trade_request INSERT 후
    ↓
HistoryService.record() 호출
    ↓
history 테이블에 TRADE_CREATED 기록

거래 상태 변경 (APPROVED/REJECTED/PENDING)
    ↓
TradeRequestService.updateStatus()
    ↓
trade_request 상태 UPDATE 후
    ↓
HistoryService.record() 호출
    ↓
history 테이블에 TRADE_STATUS_{STATUS} 기록

히스토리 조회
    ↓
GET /api/history/user/{userId}
GET /api/history/reservation/{reservationId}
GET /api/history/trade/{tradeId}
    ↓
HistoryMapper.* 로 history 테이블 조회
```

#### 시나리오 2: 사용자별 결제 대기 예약 확인
```
1. 특정 사용자의 결제 대기 중인 예약 조회
   GET /api/reservations/user/1/payment-status/대기중
   → 해당 사용자의 미결제 예약 목록 반환

2. 결제 완료 처리
   PATCH /api/reservations/1/payment-status?paymentStatus=결제완료
```

#### 시나리오 3: 결제 상태별 통계 및 관리
```
1. 전체 결제 대기 예약 조회
   GET /api/reservations/payment-status/대기중
   → 관리자가 미결제 예약 현황 파악

2. 전체 결제 완료 예약 조회
   GET /api/reservations/payment-status/결제완료
   → 결제 완료된 예약 현황 파악
```

## 📝 추가 개선 사항

- [x] 결제 현황 관리 기능 추가
- [ ] 예약 수정 기능 구현
- [ ] 예약 검증 로직 추가
- [ ] 페이징 처리 추가
- [ ] 예외 처리 강화
- [ ] 로깅 추가
- [ ] 단위 테스트 작성

## 📄 라이선스

이 프로젝트는 데모 목적으로 작성되었습니다.

