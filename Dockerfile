# === 1단계: 빌드 전용 이미지 ===
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle Wrapper 및 프로젝트 소스 복사
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# === 2단계: 실행 전용 이미지 ===
FROM amazoncorretto:17

WORKDIR /app

# 실행에 필요한 jar 파일만 복사
ARG PROJECT_NAME=discodeit
ARG PROJECT_VERSION=1.2-M8
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 포트 노출
EXPOSE 80

# 환경변수 설정
ENV JVM_OPTS=""

# 애플리케이션 실행
CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar --spring.profiles.active=prod --server.port=80"]
