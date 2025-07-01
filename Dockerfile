########################################################################################
# 1단계: Gradle 빌드 환경
########################################################################################
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# curl 설치 (Amazon Linux 기반, yum 사용)
RUN yum update -y && \
    yum install -y curl && \
    yum clean all

# Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew bootJar

########################################################################################
# 2단계: 실행 환경
########################################################################################
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 옵션 1: 로컬 static 파일 포함
COPY --from=builder /app/src/main/resources/static /app/static

# 옵션 2: 완전히 S3 의존
# COPY --from=builder /app/src/main/resources/static /app/static

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]