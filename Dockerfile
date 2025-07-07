# ==============================
# 1단계: Builder Stage
# ==============================
# Java 17 JDK가 포함된 Amazon Corretto를 빌더로 사용
FROM amazoncorretto:17 AS builder

LABEL maintainer="huindol" \
      description="Spring Boot Discodeit - Basic Build" \
      version="1.2-M8"

# 작업 디렉토리 설정
WORKDIR /app

# =============================
# Gradle 캐시 활용을 위한 단계 분리
# =============================

# Gradle Wrapper 및 설정 파일 먼저 복사 → 의존성 변경이 없으면 캐시 재사용
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성만 먼저 받아둠 → 이후 소스가 바뀌어도 재빌드 대상 최소화
RUN ./gradlew dependencies --no-daemon

# =============================
# 애플리케이션 소스 복사 및 빌드
# =============================

COPY src src

# ==============================
# 2단계: Runtime Stage (경량 이미지)
# ==============================
# Java 17 JRE만 포함된 Alpine 기반 이미지 사용 → 경량
FROM amazoncorretto:17-alpine

# 실행 디렉토리
WORKDIR /app

# 애플리케이션 빌드 (bootJar 사용)
RUN ./gradlew bootJar

# 1단계에서 생성된 JAR 파일만 복사
COPY --from=builder /app/build/libs/*.jar ./app.jar

# 컨테이너 외부에서 접근할 포트
EXPOSE 80

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
# JVM 설정 및 Spring profile 지정
ENV JVM_OPTS="-Xmx384m -Xms256m -XX:MaxMetaspaceSize=256m -XX:+UseSerialGC -Dspring.profiles.active=dev"

# 앱 실행 명령어
CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
