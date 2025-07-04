########################################################################################
# 1단계: Gradle 빌드 환경 (Amazon Corretto 17, 멀티스테이지)
########################################################################################
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle Wrapper 및 설정파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (bootJar)
RUN ./gradlew bootJar

########################################################################################
# 2단계: 최적화된 런타임 환경 (Amazon Corretto 17 JRE)
########################################################################################
FROM amazoncorretto:17

WORKDIR /app

# curl 설치 - Amazon Corretto 기본이미지는 yum 사용 가능(Alpine과 다름)
RUN yum install -y curl || true

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

########################################################################################
# 이미지 파일 처리 옵션 (로컬테스트/S3)
########################################################################################

# 옵션 1: static 파일 포함 (실습/테스트)
COPY --from=builder /app/src/main/resources/static /app/static

# 옵션 2: S3에만 의존 (프로덕션/용량최소)
# COPY --from=builder /app/src/main/resources/static /app/static

########################################################################################
# 환경변수/포트/ENTRYPOINT
########################################################################################

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
