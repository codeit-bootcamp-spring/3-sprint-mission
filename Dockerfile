# 1. 빌드
FROM amazoncorretto:17-alpine-jdk AS builder

ENV PROJECT_NAME=3-sprint-mission
ENV PROJECT_VERSION=3.0-M12

WORKDIR /app

# Gradle 캐시 최적화: wrapper와 빌드 스크립트 먼저 복사
COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true

# .dockerignore를 활용해 불필요한 파일 제외하고 복사
COPY . .

# 애플케이션 빌드
RUN ./gradlew --no-daemon clean bootJar

# 2. 런타임
# 환경 변수 설정
FROM amazoncorretto:17-alpine

ENV PROJECT_NAME=3-sprint-mission
ENV PROJECT_VERSION=3.0-M12
ENV JVM_OPTS=""

RUN apk add --no-cache curl wget

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 스테이지에서 JAR 파일만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
