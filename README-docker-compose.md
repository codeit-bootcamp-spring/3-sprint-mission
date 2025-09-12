# Docker Compose 사용법

## 기본 구조
- 여러 서비스(PostgreSQL, Redis, Kafka, 애플리케이션, Nginx 등)를 컨테이너로 관리합니다.
- 네트워크는 `discodeit-network`로 통합되어 있습니다.

## 주요 파일
- `docker-compose.yml`: 전체 서비스(앱, DB, 프록시 등) 정의
- `docker-compose-dev.yml`: 개발 환경용 포트 오버라이드
- `docker-compose-redis.yml`: Redis 서비스 정의
- `docker-compose-kafka.yml`: Kafka 서비스 정의
- `run-script/`: 실행/종료 스크립트 모음

## 실행 예시

### 전체 서비스 실행
```bash
# 개발 환경 전체 실행 (Kafka, Redis, Dev 설정 포함)
docker-compose -f docker-compose-kafka.yml -f docker-compose-redis.yml -f docker-compose-dev.yml -f docker-compose.yml up -d
```

### 개별 서비스 실행
```bash
# Kafka, Redis만 실행
docker-compose -f docker-compose-kafka.yml -f docker-compose-redis.yml up -d
```

### 종료
```bash
# 전체 서비스 종료
docker-compose -f docker-compose-kafka.yml -f docker-compose-redis.yml -f docker-compose-dev.yml -f docker-compose.yml down
```

## 실행 스크립트
- `run-script/start-all.sh`: 전체 서비스 실행
- `run-script/down-all.sh`: 전체 서비스 종료
- `run-script/start-kafka-and-redis.sh`: Kafka/Redis만 실행
- `run-script/down-kafka-and-redis.sh`: Kafka/Redis만 종료
- `run-script/build-all.sh`: 전체 빌드

## 환경 변수
- `.env` 파일 또는 시스템 환경 변수에서 각종 설정을 읽어옵니다.
- 주요 변수: DB, S3, JWT, Kafka, Redis, Admin 계정 등

## 주의사항
- 컨테이너 빌드/실행 전 `.env` 파일을 반드시 프로젝트 루트에 준비하세요.
- `depends_on`으로 DB가 정상적으로 올라온 뒤 앱이 실행됩니다.
- 개발/테스트/운영 환경에 따라 `SPRING_PROFILES_ACTIVE` 등 환경 변수를 조정하세요.

---
자세한 설정/오버라이드는 각 docker-compose 파일을 참고하세요.
