# 1단계: 빌드 스테이지
FROM amazoncorretto:17 AS builder

WORKDIR /app

# 환경변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

COPY . .

RUN chmod +x ./gradlew && ./gradlew bootJar && \
    cp build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

ENV JVM_OPTS=""
EXPOSE 80

RUN apk add --no-cache curl

COPY --from=builder /app/app.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
