# 🛠️ Discodeit

---
Spring Boot 기반의 메시징 시스템 프로젝트

[![codecov](https://codecov.io/gh/hyohyo-zz/3-sprint-mission/branch/main/graph/badge.svg?token=OU6EN7ZCG3)](https://codecov.io/gh/hyohyo-zz/3-sprint-mission)

## 📌 프로젝트 개요

Discodeit은 채널 기반의 커뮤니케이션 서비스를 제공하는 백엔드 서버입니다.
Public / Private 채널 생성, 메시지 전송, 사용자 상태 추적 등의 기능을 지원합니다.

---

## ⚙️ 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.4.4
- **Database**: PostgreSQL (dev, prod), H2 (test)
- **Storage**: Local File System, AWS S3
- **ORM**: Spring Data JPA (Hibernate)
- **API Docs**: Springdoc OpenAPI (Swagger UI)
- **빌드 도구**: Gradle
- **로깅**: Logback, SLF4J + AOP 기반 로깅

---

## 🧩 프로파일 기반 설정

### Development (`dev`)

- **Database**: PostgreSQL (localhost:5432)
- **Port**: 8080
- **Storage**: Local file system
- **Logging**: Debug level, SQL 쿼리 출력

### Production (`prod`)

- **Database**: PostgreSQL (AWS RDS)
- **Port**: 80
- **Storage**: AWS S3
- **Logging**: Info level, SQL 로깅 비활성화

### Test (`test`)

- **Database**: H2 in-memory (PostgreSQL 호환 모드)
- **JPA**: `ddl-auto: create` (테이블 자동 생성)
- **Storage**: 테스트용 Mock/Local
- **Logging**: Debug level, 상세 SQL 로깅

---

## 🔗 주요 API 엔드포인트

| 기능     | Method | Endpoint                       | 설명                     |
|--------|--------|--------------------------------|------------------------|
| 사용자 생성 | POST   | `/api/users`                   | 사용자 등록                 |
| 채널 생성  | Get    | `/api/channels`                | PUBLIC / PRIVATE 채널 생성 |
| 메시지 전송 | POST   | `/api/messages`                | 채널에 메시지 전송             |
| 메시지 목록 | GET    | `/api/messages?channelId={id}` | 특정 채널의 메시지 조회          ||

---

## 🧾 패키지 구조

```
com.sprint.mission.discodeit
├── aop                     # 공통 로깅/트랜잭션을 위한 AOP 설정
├── common                  # 상수, 유틸리티, 공용 포맷 등
├── config                  # Swagger, WebMvc, 로깅 등 설정 클래스
├── controller              # API 컨트롤러 계층
│   └── api
├── dto                     # 요청/응답용 DTO 계층
│   ├── data                # 내부 응답 또는 공용 데이터 구조
│   ├── request             # 클라이언트 요청 DTO
│   └── response            # API 응답 DTO
├── entity                  # JPA 엔티티 클래스
│   └── base
├── exception               # 커스텀 예외 및 예외 계층 구조
├── mapper                  # DTO ↔ Entity 변환 전용 매퍼
├── repository              # JPA Repository 인터페이스 정의
├── service                 # 비즈니스 로직 계층
│   ├── basic
├── storage
│   └── local               # 파일 저장소 구현체 (로컬)
│   └── s3                  # AWS S3 구현체
└── DiscodeitApplication    # 스프링 부트 메인 애플리케이션
```

---

## 🔧 환경 설정

```yaml
  discodeit:
  storage:
  type: ${STORAGE_TYPE:local}  # local | s3
  local:
  root-path: ${STORAGE_LOCAL_ROOT_PATH:.discodeit/storage}
  s3:
  access-key: ${AWS_S3_ACCESS_KEY}
  secret-key: ${AWS_S3_SECRET_KEY}
  region: ${AWS_S3_REGION}
  bucket: ${AWS_S3_BUCKET}
  presigned-url-expiration: ${AWS_S3_PRESIGNED_URL_EXPI
```

---

## 🧵 추가 구성

- `logback-spring.xml` 설정 완료
    - `.logs` 디렉토리에 일자별 파일 로그 저장
    - 콘솔 + 파일 동시 출력
