# 경량화
# ------------------------------------------------------------
# 1단계: Gradle 빌드 환경
# ------------------------------------------------------------

FROM amazoncorretto:17 AS builder

WORKDIR /app

RUN yum update -y && \
    yum install -y curl && \
    yum clean all

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN ./gradlew bootJar

# ------------------------------------------------------------
# 2단계: 실행 환경
# ------------------------------------------------------------
FROM amazoncorretto:17-alpine3.21-jdk

WORKDIR /app

# JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 필요시 정적 리소스도 복사 (안 쓰면 생략 가능)
 COPY --from=builder /app/src/main/resources/static /app/static

# 환경 변수 및 포트
ENV JVM_OPTS=""
EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
