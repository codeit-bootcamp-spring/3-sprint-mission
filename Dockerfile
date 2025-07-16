########################################################################################
# 1단계: Gradle 빌드 환경 (멀티플랫폼 지원)
########################################################################################
FROM amazoncorretto:17 AS builder

# 작업 디렉토리
WORKDIR /app

# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Gradle 래퍼 실행 권한 부여
# Linux 환경에서 Gradle 래퍼 스크립트 실행을 위해 필요
RUN chmod +x gradlew

# 의존성 다운로드 (별도 단계로 분리하여 캐싱 효과 극대화)
# 소스 코드가 변경되어도 의존성은 다시 다운로드하지 않음
RUN ./gradlew dependencies --no-daemon --quiet

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드 (bootJar 사용)
RUN ./gradlew clean build -x test

########################################################################################
# 2단계: 실행 환경 (최적화된 런타임)
########################################################################################
FROM amazoncorretto:17

LABEL maintainer="jiin" \
      description="Spring Boot discodeit" \
      version="1.2-M8"

WORKDIR /app

RUN mkdir -p /app/.logs/prod && chmod -R 755 /app/.logs/prod

# JVM 옵션 환경 변수 설정
ENV PROJECT_NAME=discodeit \
     PROJECT_VERSION=1.2-M8 \
     JVM_OPTS=""

COPY --from=builder /app/build/libs/discodeit-1.2-M8.jar /app/app.jar

# 기본 이미지 파일 처리
COPY --from=builder /app/src/main/resources/static/images /app/static/images

########################################################################################
# 컨테이너 실행 설정
########################################################################################
# 포트 노출
EXPOSE 80

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/app.jar --spring.profiles.active=prod --server.port=80"]