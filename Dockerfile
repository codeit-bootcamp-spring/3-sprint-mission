########################################################################################
# 1단계: Gradle 빌드 환경
########################################################################################
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

########################################################################################
# 2단계: 실행 환경
########################################################################################
FROM amazoncorretto:17

WORKDIR /app

# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 실행할 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 정적 파일 복사 (옵션 1: 포함)
COPY --from=builder /app/src/main/resources/static /app/static
# 정적 파일 복사 (옵션 2: 완전한 S3 의존)
# COPY --from=builder /app/src/main/resources/static /app/static

# 80 포트 노출
EXPOSE 80

# 실행 명령 설정 - 환경 변수 활용
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
