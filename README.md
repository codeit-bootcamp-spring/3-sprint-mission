# Pure - Discord Clone Application [![codecov](https://codecov.io/gh/pureod/3-sprint-mission/branch/sprint8/graph/badge.svg?token=4IDIFWEUFI)](https://codecov.io/gh/pureod/3-sprint-mission)

Spring Boot 기반의 실시간 채팅 애플리케이션입니다. Discord와 유사한 기능들을 제공하는 RESTful API 서버입니다.

## 🚀 주요 기능

### 📱 핵심 기능

- **사용자 관리**: 회원가입, 로그인, 프로필 관리
- **채널 관리**: 공개/비공개 채널 생성 및 관리
- **실시간 메시징**: 채널 내 메시지 송수신
- **파일 첨부**: 이미지 및 파일 업로드 지원
- **읽음 상태**: 메시지 읽음/안읽음 상태 관리
- **사용자 상태**: 온라인/오프라인 상태 표시

### 🛠 기술적 특징

- **MapStruct 매핑**: Entity-DTO 간 효율적인 객체 변환
- **Spring Data JPA**: 데이터베이스 액세스 계층
- **PostgreSQL**: 메인 데이터베이스
- **Spring HATEOAS**: RESTful API 하이퍼미디어 지원
- **OpenAPI 3.0**: API 문서화 (Swagger UI)
- **Spring Validation**: 입력 데이터 검증
- **Spring AOP**: 횡단 관심사 처리

## 🔧 기술 스택

### Backend

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **Spring Web MVC**
- **MapStruct 1.5.5**
- **Lombok**

### Database

- **PostgreSQL** (운영환경)
- **JPA/Hibernate** (ORM)

### Documentation & Testing

- **SpringDoc OpenAPI 3** (API 문서화)
- **JUnit 5** (테스트 프레임워크)

## 🚀 시작하기

### 필수 요구사항

- Java 17 이상
- PostgreSQL 데이터베이스
- Gradle 7.x 이상

### 설치 및 실행

1. **프로젝트 클론******
   bash git clone <repository-url> cd discodeit

2. **데이터베이스 설정**

```bash
# PostgreSQL 데이터베이스 생성
createdb discodeit
```

3. **환경변수 설정** (`application.yml` 또는 환경변수로 설정)

## 📋 API 엔드포인트

### 사용자 관리

- `POST /api/users` - 사용자 생성
- `GET /api/users/{id}` - 사용자 조회
- `PUT /api/users/{id}` - 사용자 정보 수정
- `DELETE /api/users/{id}` - 사용자 삭제

### 채널 관리

- `POST /api/channels` - 채널 생성
- `GET /api/channels` - 채널 목록 조회
- `GET /api/channels/{id}` - 채널 상세 조회

### 메시지 관리

- `POST /api/channels/{channelId}/messages` - 메시지 전송
- `GET /api/channels/{channelId}/messages` - 메시지 조회

