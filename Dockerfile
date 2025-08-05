# ---------- [1단계] 빌드 스테이지 ----------
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle 설정 파일 및 Wrapper 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Gradle 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 캐싱
RUN ./gradlew dependencies || true

# 전체 프로젝트 복사
COPY . .

# 테스트 제외 빌드
RUN ./gradlew clean build -x test

# ---------- [2단계] 런타임 스테이지 ----------
FROM amazoncorretto:17-alpine AS runtime

WORKDIR /app

# 빌드 결과물 복사
COPY --from=builder /app/build/libs/discodeit-1.2-M8.jar app.jar

# 포트 노출
EXPOSE 80

# 환경 변수
ENV JVM_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

# 실행 명령
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]