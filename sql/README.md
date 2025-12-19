# FitNeeds DB SQL 스크립트

이 폴더에는 FitNeeds 데이터베이스의 스키마 생성 및 샘플 데이터 삽입을 위한 SQL 파일이 포함되어 있습니다.

## 파일 목록

### 1. `schema.sql`
- 데이터베이스 테이블 스키마 생성 SQL
- 모든 테이블 생성 및 인덱스 설정 포함

### 2. `seed_data.sql`
- 샘플 데이터 삽입 SQL
- 테스트 및 개발 환경에서 사용할 가데이터 포함

## 사용 방법

### 데이터베이스 연결 정보
- **DB 이름**: `fitneedsdb`
- **계정**: `fitneeds` / `fullstack2025`
- **호스트**: `fitneedsdb.czqm4wwgq577.ap-northeast-2.rds.amazonaws.com:3306`

### 로컬 환경
- **계정**: `root` / `1234`
- **호스트**: `localhost:3306`

### 실행 순서

1. **스키마 생성**
   ```bash
   mysql -u fitneeds -p fitneedsdb < sql/schema.sql
   ```

2. **샘플 데이터 삽입**
   ```bash
   mysql -u fitneeds -p fitneedsdb < sql/seed_data.sql
   ```

### MariaDB CLI 사용 예시

```bash
# 데이터베이스 접속
mysql -u fitneeds -p -h fitneedsdb.czqm4wwgq577.ap-northeast-2.rds.amazonaws.com

# 데이터베이스 선택
USE fitneedsdb;

# 스키마 생성
SOURCE sql/schema.sql;

# 샘플 데이터 삽입
SOURCE sql/seed_data.sql;
```

## 테이블 구조

1. **USER** - 회원 정보
2. **SCHEDULE** - 운동 스케줄
3. **TICKET** - 이용권
4. **RESERVATION** - 예약
5. **PAYMENT** - 결제
6. **ATTENDANCE** - 출석
7. **REVIEW** - 리뷰

## 주의사항

- 스키마 생성 전에 기존 테이블이 있다면 삭제하거나 백업하세요.
- 샘플 데이터는 테스트용이므로 프로덕션 환경에서는 사용하지 마세요.
- 외래키 제약조건이 설정되어 있으므로 데이터 삽입 순서를 지켜주세요.

