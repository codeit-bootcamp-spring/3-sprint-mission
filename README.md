# 🛠️ Discodeit

---
Spring Boot 기반의 메시징 시스템 프로젝트

## 📌 프로젝트 개요

Discodeit은 채널 기반의 커뮤니케이션 서비스를 제공하는 백엔드 서버입니다.
Public / Private 채널 생성, 메시지 전송, 사용자 상태 추적 등의 기능을 지원합니다.

---

## ⚙️ 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.4.4
- **Database**: H2 (dev), PostgreSQL (prod)
- **ORM**: Spring Data JPA (Hibernate)
- **API Docs**: Springdoc OpenAPI (Swagger UI)
- **빌드 도구**: Gradle
- **로깅**: Logback, SLF4J + AOP 기반 로깅

---

## 🧩 프로파일 기반 설정

- `application-dev.yaml`: H2 DB, 서버 포트 8080
- `application-prod.yaml`: PostgreSQL, 서버 포트 8080

모든 프로파일은 공통 설정을 `application.yaml`에서 상속합니다.

---

## 🚀 실행 방법

### 1. 개발 환경 (H2 사용)

### 2. 운영 환경 (PostgreSql 사용)

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
└── DiscodeitApplication    # 스프링 부트 메인 애플리케이션
```

---

## 🧵 추가 구성

- `logback-spring.xml` 설정 완료
    - `.logs` 디렉토리에 일자별 파일 로그 저장
    - 콘솔 + 파일 동시 출력