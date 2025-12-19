# API 명세서

**작성자**: 이정재  
**최종 수정일**: 2025-12-17  
**주요 변경사항**: 무통장입금 결제 시스템, 출석체크 기능, 출석현황 조회 기능 추가, 마이페이지용 이용권 조회 API 추가  
**참고**: 이 API 명세서는 마이페이지(End User)용 API입니다. 예약 생성 및 관리 기능은 별도의 관리자 서비스에서 제공됩니다.

---

## API 목록

| API 명(영어) | API 명(한글) | 설명 |
|------------|------------|------|
| getMyReservations() | 나의 예약 목록 조회 | 로그인한 사용자의 모든 예약 목록을 조회한다 (마이페이지) |
| getMyCompletedReservations() | 결제완료된 예약 목록 조회 (이용내역) | 결제완료된 예약 목록을 조회한다 (이용내역 화면용) |
| cancelReservation() | 예약 취소 | 예약을 취소한다. 예약 상태를 CANCELLED로 변경합니다 |
| getMyTickets() | 나의 이용권 목록 조회 | 나의 이용권 목록을 조회한다 (마이페이지용) |
| checkAttendanceByUser() | 사용자 출석체크 (기간권) | 기간권 이용권의 경우 사용자가 직접 출석체크를 한다 |
| getMyAttendances() | 나의 출석현황 조회 | 나의 출석현황 목록을 조회한다 |
| updateAttendanceStatus() | 출석 상태 변경 | 출석 상태를 변경한다 (출석, 결석, 취소) |
| getMyReviews() | 나의 리뷰 목록 조회 | 나의 리뷰 목록을 조회한다 (마이페이지) |
| getReviewByReservationId() | 예약 ID로 리뷰 조회 | 예약 ID로 해당 예약의 리뷰를 조회한다 |
| createReview() | 리뷰 생성 | 새로운 리뷰를 작성한다 (이용내역의 결제완료된 예약에 대해서만 작성 가능) |
| updateReview() | 리뷰 수정 | 기존 리뷰를 수정한다 |
| deleteReviewById() | 리뷰 삭제 | 리뷰를 삭제한다 |

---

## 목차
1. [나의 예약 목록 조회](#1-나의-예약-목록-조회)
2. [결제완료된 예약 목록 조회 (이용내역)](#2-결제완료된-예약-목록-조회-이용내역)
3. [예약 취소](#3-예약-취소)
4. [나의 이용권 목록 조회](#4-나의-이용권-목록-조회)
5. [사용자 출석체크 (기간권)](#5-사용자-출석체크-기간권)
6. [나의 출석현황 조회](#6-나의-출석현황-조회)
7. [출석 상태 변경](#7-출석-상태-변경)
8. [리뷰 작성 및 조회](#8-리뷰-작성-및-조회)

---

## 1. 나의 예약 목록 조회

### 1.1 API 정보
- **URL**: `/api/reservation/my`
- **Method**: `GET`
- **Controller 메서드**: `getMyReservations()`
- **설명**: 나의 예약 목록을 조회한다 (마이페이지)

### 1.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
없음

#### Body
없음

### 1.3 Response

#### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| - | 데이터 배열 | Array | Y |

배열 내 각 객체:
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| reservationId | 예약 ID | Long | Y |
| exerciseName | 운동명 | String | Y |
| exerciseDate | 운동 날짜/시간 | String (ISO DateTime) | Y |
| exerciseLocation | 운동 장소 | String | Y |
| trainerName | 트레이너 이름 | String | Y |
| paymentStatus | 결제 상태 | String | N |

**paymentStatus 값**:
- `BANK_TRANSFER_PENDING`: 입금대기
- `BANK_TRANSFER_COMPLETED`: 입금완료
- `null`: 결제 정보 없음

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 1.4 Response 예시

#### 성공
```json
{
  "status": "SUCCESS",
  "message": "조회 성공",
  "data": [
    {
      "reservationId": 1,
      "exerciseName": "요가 클래스",
      "exerciseDate": "2025-12-20T10:00:00",
      "exerciseLocation": "강남점",
      "trainerName": "김트레이너",
      "paymentStatus": "BANK_TRANSFER_COMPLETED"
    },
    {
      "reservationId": 2,
      "exerciseName": "필라테스",
      "exerciseDate": "2025-12-22T14:00:00",
      "exerciseLocation": "서초점",
      "trainerName": "이트레이너",
      "paymentStatus": "BANK_TRANSFER_PENDING"
    },
    {
      "reservationId": 3,
      "exerciseName": "헬스",
      "exerciseDate": "2025-12-25T18:00:00",
      "exerciseLocation": "송파점",
      "trainerName": "박트레이너",
      "paymentStatus": null
    }
  ]
}
```

#### 실패
```json
{
  "status": "E0102",
  "message": "예약 목록 조회 중 오류가 발생했습니다."
}
```

---

## 2. 결제완료된 예약 목록 조회 (이용내역)

### 2.1 API 정보
- **URL**: `/api/reservation/my/completed`
- **Method**: `GET`
- **Controller 메서드**: `getMyCompletedReservations()`
- **설명**: 결제완료된 예약 목록을 조회한다 (이용내역 화면용)

### 2.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
없음

#### Body
없음

### 2.3 Response

#### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| - | 데이터 배열 | Array | Y |

배열 내 각 객체는 [나의 예약 목록 조회](#1-나의-예약-목록-조회) Response Body와 동일  
단, `payment.paymentStatus`가 "BANK_TRANSFER_COMPLETED" (입금완료)인 예약만 포함

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 2.4 Response 예시

#### 성공
```json
{
  "status": "SUCCESS",
  "message": "조회 성공",
  "data": [
    {
      "reservationId": 1,
      "exerciseName": "요가 클래스",
      "exerciseDate": "2025-12-20T10:00:00",
      "exerciseLocation": "강남점",
      "trainerName": "김트레이너",
      "paymentStatus": "BANK_TRANSFER_COMPLETED"
    }
  ]
}
```

#### 실패
```json
{
  "status": "E0102",
  "message": "결제완료 예약 목록 조회 중 오류가 발생했습니다."
}
```

---

## 3. 예약 취소

### 3.1 API 정보
- **URL**: `/api/reservation/{reservationId}/cancel`
- **Method**: `PATCH`
- **Controller 메서드**: `cancelReservation()`
- **설명**: 예약을 취소한다 (마이페이지). 예약 상태를 CANCELLED로 변경합니다.
- **권한**: 인증된 사용자만 자신의 예약을 취소할 수 있음
- **참고**: DELETE가 아닌 PATCH를 사용하는 이유는 예약을 삭제하는 것이 아니라 상태를 변경하기 때문입니다.

### 3.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| reservationId | 예약 ID | Long | Y | - |

#### Body
없음

### 3.3 Response

#### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| message | 결과 메시지 | String | Y |

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 3.4 Response 예시

#### 성공
```json
{
  "status": "SUCCESS",
  "message": "예약이 취소되었습니다."
}
```

#### 실패
```json
{
  "status": "E0001",
  "message": "예약을 찾을 수 없습니다."
}
```

---

## 4. 나의 이용권 목록 조회

### 4.1 API 정보
- **URL**: `/api/ticket/my`
- **Method**: `GET`
- **Controller 메서드**: `getMyTickets()`
- **설명**: 나의 이용권 목록을 조회한다 (마이페이지용)

### 4.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
없음

#### Body
없음

### 4.3 Response

#### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| - | 데이터 배열 | Array | Y |

배열 내 각 객체:
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| ticketId | 이용권 ID | Long | Y |
| ticketType | 이용권 종류 | String | Y |
| remainingCount | 남은 횟수 | Integer | N |
| expiryDate | 만료일 | String (ISO DateTime) | N |

**ticketType 값**:
- `PERIOD`: 기간권
- `COUNT`: 횟수권

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 4.4 Response 예시

#### 성공
```json
{
  "status": "SUCCESS",
  "message": "조회 성공",
  "data": [
    {
      "ticketId": 1,
      "ticketType": "PERIOD",
      "remainingCount": null,
      "expiryDate": "2026-12-31T23:59:59"
    },
    {
      "ticketId": 2,
      "ticketType": "COUNT",
      "remainingCount": 5,
      "expiryDate": null
    }
  ]
}
```

#### 실패
```json
{
  "status": "E0102",
  "message": "이용권 목록 조회 중 오류가 발생했습니다."
}
```

---

## 5. 사용자 출석체크 (기간권)

### 5.1 API 정보
- **URL**: `/api/attendance/reservation/{reservationId}/check`
- **Method**: `POST`
- **Controller 메서드**: `checkAttendanceByUser()`
- **설명**: 기간권 이용권의 경우 사용자가 직접 출석체크를 한다

### 5.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| reservationId | 예약 ID | Long | Y | - |

#### Body
없음

### 5.3 Response

#### Body
[사용자 출석체크 (기간권)](#5-사용자-출석체크-기간권) Response Body와 동일

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 5.4 Response 예시

#### 성공
```json
{
  "status": "SUCCESS",
  "message": "출석체크가 완료되었습니다.",
  "data": {
    "attendanceId": 1,
    "reservationId": 1,
    "ticketId": 5,
    "attendanceDate": "2025-12-20",
    "attendanceStatusCode": "ATTEND"
  }
}
```

#### 실패
```json
{
  "status": "E0001",
  "message": "기간권만 사용자가 출석체크할 수 있습니다."
}
```

---

## 6. 나의 출석현황 조회

### 6.1 API 정보
- **URL**: `/api/attendance/my`
- **Method**: `GET`
- **Controller 메서드**: `getMyAttendances()`
- **설명**: 나의 출석현황 목록을 조회한다

### 6.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
없음

#### Body
없음

### 6.3 Response

#### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| - | 데이터 배열 | Array | Y |

배열 내 각 객체:
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| attendanceId | 출석 ID | Long | Y |
| reservationId | 예약 ID | Long | N |
| ticketId | 이용권 ID | Long | Y |
| attendanceDate | 출석일 | String (ISO Date) | Y |
| attendanceStatusCode | 출석상태 | String | Y |
| modifyDateTime | 수정일시 | String (ISO DateTime) | N |
| modifyUserId | 수정자ID | String | N |

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 6.4 Response 예시

#### 성공

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
없음


#### 성공
```json
{
  "status": "SUCCESS",
  "message": "조회 성공",
  "data": [
    {
      "attendanceId": 1,
      "reservationId": 1,
      "ticketId": 5,
      "attendanceDate": "2025-12-20",
      "attendanceStatusCode": "ATTEND"
    },
    {
      "attendanceId": 2,
      "reservationId": 2,
      "ticketId": 5,
      "attendanceDate": "2025-12-22",
      "attendanceStatusCode": "ABSENT"
    }
  ]
}
```

#### 실패
```json
{
  "status": "E0102",
  "message": "출석현황 조회 중 오류가 발생했습니다."
}
```

---

## 7. 출석 상태 변경

### 7.1 API 정보
- **URL**: `/api/attendance/{attendanceId}/status`
- **Method**: `PUT`
- **Controller 메서드**: `updateAttendanceStatus()`
- **설명**: 출석 상태를 변경한다 (출석, 결석, 취소)

### 7.2 Request

#### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

#### Query
없음

#### Path
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| attendanceId | 출석 ID | Long | Y | - |

#### Body
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| attendanceStatusCode | 출석상태 | String | Y | - |

**attendanceStatusCode 값**:
- `ATTEND`: 출석
- `ABSENT`: 결석
- `CANCEL`: 취소

### 7.3 Response

#### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| message | 결과 메시지 | String | Y |

#### resultCode
- **설명**: 결과 상태
- **필수**: Y

#### message
- **설명**: 결과 메시지
- **필수**: Y

### 7.4 Response 예시

#### 성공
```json
{
  "status": "SUCCESS",
  "message": "출석 상태가 변경되었습니다."
}
```

#### 실패
```json
{
  "status": "E0001",
  "message": "출석 정보를 찾을 수 없습니다."
}
```

---

## 8. 리뷰 작성 및 조회

### 8.1 나의 리뷰 목록 조회

#### 8.1.1 API 정보
- **URL**: `/api/reviews/my`
- **Method**: `GET`
- **Controller 메서드**: `getMyReviews()`
- **설명**: 나의 리뷰 목록을 조회한다 (마이페이지)
- **권한**: 인증된 사용자만 자신의 리뷰를 조회할 수 있음

#### 8.1.2 Request

##### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

##### Query
없음

##### Path
없음

##### Body
없음

#### 8.1.3 Response

##### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| - | 데이터 배열 | Array | Y |

배열 내 각 객체:
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| reviewId | 리뷰 ID | Long | Y |
| reservationId | 예약 ID | Long | Y |
| rating | 별점 | Integer | Y |
| content | 후기 내용 | String | N |
| instructorId | 강사 ID | Long | Y |
| registrationDateTime | 작성일 | String (ISO DateTime) | Y |

#### 8.1.4 Response 예시

##### 성공
```json
{
  "status": "SUCCESS",
  "message": "조회 성공",
  "data": [
    {
      "reviewId": 1,
      "reservationId": 1,
      "rating": 5,
      "content": "정말 좋은 수업이었습니다!",
      "instructorId": 1,
      "registrationDateTime": "2025-12-20T15:30:00"
    },
    {
      "reviewId": 2,
      "reservationId": 2,
      "rating": 4,
      "content": "만족스러운 수업입니다.",
      "instructorId": 1,
      "registrationDateTime": "2025-12-22T16:00:00"
    }
  ]
}
```

##### 실패
```json
{
  "status": "E0102",
  "message": "리뷰 목록 조회 중 오류가 발생했습니다."
}
```

---

### 8.2 예약 ID로 리뷰 조회

#### 8.2.1 API 정보
- **URL**: `/api/reviews/reservation/{reservationId}`
- **Method**: `GET`
- **Controller 메서드**: `getReviewByReservationId()`
- **설명**: 예약 ID로 해당 예약의 리뷰를 조회한다
- **권한**: 인증된 사용자만 자신의 예약에 대한 리뷰를 조회할 수 있음

#### 8.2.2 Request

##### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

##### Query
없음

##### Path
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| reservationId | 예약 ID | Long | Y | - |

##### Body
없음

#### 8.2.3 Response

##### Body
[나의 리뷰 목록 조회](#811-나의-리뷰-목록-조회) Response Body와 동일 (배열)

#### 8.2.4 Response 예시

##### 성공
```json
{
  "status": "SUCCESS",
  "message": "조회 성공",
  "data": [
    {
      "reviewId": 1,
      "reservationId": 1,
      "rating": 5,
      "content": "정말 좋은 수업이었습니다!",
      "instructorId": 1,
      "registrationDateTime": "2025-12-20T15:30:00"
    }
  ]
}
```

##### 실패
```json
{
  "status": "E0001",
  "message": "리뷰를 찾을 수 없습니다."
}
```

---

### 8.3 리뷰 생성

#### 8.3.1 API 정보
- **URL**: `/api/reviews`
- **Method**: `POST`
- **Controller 메서드**: `createReview()`
- **설명**: 새로운 리뷰를 작성한다 (이용내역의 결제완료된 예약에 대해서만 작성 가능)
- **권한**: 인증된 사용자만 자신의 예약에 대한 리뷰를 작성할 수 있음
- **제한사항**: 
  - 결제완료된 예약(`BANK_TRANSFER_COMPLETED`)에 대해서만 리뷰 작성 가능
  - 한 예약당 하나의 리뷰만 작성 가능

#### 8.3.2 Request

##### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

##### Query
없음

##### Path
없음

##### Body
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| reservationId | 예약 ID | Long | Y | - |
| rating | 별점 | Integer | Y | - |
| content | 후기 내용 | String | N | - |
| instructorId | 강사 ID | Long | Y | - |

**rating 값**:
- 1 ~ 5 사이의 정수

#### 8.3.3 Response

##### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| reviewId | 리뷰 ID | Long | Y |
| reservationId | 예약 ID | Long | Y |
| rating | 별점 | Integer | Y |
| content | 후기 내용 | String | N |
| instructorId | 강사 ID | Long | Y |
| registrationDateTime | 작성일 | String (ISO DateTime) | Y |

#### 8.3.4 Response 예시

##### 성공
```json
{
  "status": "SUCCESS",
  "message": "리뷰가 작성되었습니다.",
  "data": {
    "reviewId": 1,
    "reservationId": 1,
    "rating": 5,
    "content": "정말 좋은 수업이었습니다!",
    "instructorId": 1,
    "registrationDateTime": "2025-12-20T15:30:00"
  }
}
```

##### 실패
```json
{
  "status": "E0401",
  "message": "결제완료된 예약만 리뷰를 작성할 수 있습니다."
}
```

또는

```json
{
  "status": "E0401",
  "message": "이미 리뷰가 작성된 예약입니다."
}
```

또는

```json
{
  "status": "E0201",
  "message": "리뷰 작성 권한이 없습니다."
}
```

---

### 8.4 리뷰 수정

#### 8.4.1 API 정보
- **URL**: `/api/reviews/{reviewId}`
- **Method**: `PUT`
- **Controller 메서드**: `updateReview()`
- **설명**: 기존 리뷰를 수정한다
- **권한**: 인증된 사용자만 자신이 작성한 리뷰를 수정할 수 있음

#### 8.4.2 Request

##### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

##### Query
없음

##### Path
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| reviewId | 리뷰 ID | Long | Y | - |

##### Body
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| rating | 별점 | Integer | Y | - |
| content | 후기 내용 | String | N | - |

**rating 값**:
- 1 ~ 5 사이의 정수

#### 8.4.3 Response

##### Body
[리뷰 생성](#831-리뷰-생성) Response Body와 동일

#### 8.4.4 Response 예시

##### 성공
```json
{
  "status": "SUCCESS",
  "message": "리뷰가 수정되었습니다.",
  "data": {
    "reviewId": 1,
    "reservationId": 1,
    "rating": 4,
    "content": "수정된 후기 내용",
    "instructorId": 1,
    "registrationDateTime": "2025-12-20T15:30:00"
  }
}
```

##### 실패
```json
{
  "status": "E0001",
  "message": "리뷰를 찾을 수 없습니다."
}
```

또는

```json
{
  "status": "E0201",
  "message": "리뷰 수정 권한이 없습니다."
}
```

---

### 8.5 리뷰 삭제

#### 8.5.1 API 정보
- **URL**: `/api/reviews/{reviewId}`
- **Method**: `DELETE`
- **Controller 메서드**: `deleteReviewById()`
- **설명**: 리뷰를 삭제한다
- **권한**: 인증된 사용자만 자신이 작성한 리뷰를 삭제할 수 있음

#### 8.5.2 Request

##### Header
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| Authorization | 인증 Token | String | Y | bearer... |

##### Query
없음

##### Path
| Name | 설명 | Type | 필수 | 기본값 |
|------|------|------|------|--------|
| reviewId | 리뷰 ID | Long | Y | - |

##### Body
없음

#### 8.5.3 Response

##### Body
| Name | 설명 | Type | 필수 |
|------|------|------|------|
| message | 결과 메시지 | String | Y |

#### 8.5.4 Response 예시

##### 성공
```json
{
  "status": "SUCCESS",
  "message": "리뷰가 삭제되었습니다."
}
```

##### 실패
```json
{
  "status": "E0001",
  "message": "리뷰를 찾을 수 없습니다."
}
```

---

## 공통 에러 코드

| 에러 코드 | 설명 |
|----------|------|
| E0001 | 리소스를 찾을 수 없음 |
| E0102 | 조회 중 오류 발생 |
| E0201 | 권한이 없음 |
| E0301 | 인증 토큰이 필요함 |
| E0401 | 잘못된 요청 |

---

## 참고사항

1. **인증**: 모든 API는 JWT 인증 토큰이 필요합니다 (회원가입/로그인 제외)
   - Authorization 헤더 형식: `Bearer {token}`
   - 인증 실패 시: 401 Unauthorized

2. **날짜 형식**: ISO 8601
   - 날짜: `2025-12-17`
   - 날짜/시간: `2025-12-17T10:00:00`

3. **결제 상태**:
   - `BANK_TRANSFER_PENDING`: 입금대기
   - `BANK_TRANSFER_COMPLETED`: 입금완료

4. **결제 수단**: 
   - `BANK_TRANSFER`: 무통장입금 (고정)

5. **예약 상태**:
   - `RESERVED`: 예약됨
   - `CANCELLED`: 취소됨
   - `COMPLETED`: 완료됨

6. **출석 상태**:
   - `ATTEND`: 출석
   - `ABSENT`: 결석
   - `CANCEL`: 취소

7. **이용권 타입**:
   - `PERIOD`: 기간권 (사용자가 출석체크)
   - `COUNT`: 횟수권 (강사가 출석체크)


