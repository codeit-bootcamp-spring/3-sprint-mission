# 1. 빌드 스테이지
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 관련 파일들만 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# 권한 부여 (중요!)
RUN chmod +x gradlew

# 의존성 다운로드 (레이어 캐시 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon

# -------------------------------------

# 2. 런타임 스테이지 (Runtime Stage)
FROM amazoncorretto:17-alpine AS runtime

# 작업 디렉토리 설정
WORKDIR /app

# 엔트리포인트 스크립트 복사
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

# 빌드된 JAR 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 80 포트 노출
EXPOSE 80

# 환경 변수 설정
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=local-slim \
    JVM_OPTS=""

# 애플리케이션 실행
ENTRYPOINT ["./entrypoint.sh"]