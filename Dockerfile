########################################################################################
# 1단계: Gradle 빌드 환경 (멀티플랫폼 지원)
########################################################################################
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Gradle Wrapper 및 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# 의존성 미리 다운(캐시효과)
RUN ./gradlew dependencies

# 애플리케이션 빌드 (bootJar)
RUN ./gradlew bootJar

########################################################################################
# 2단계: 최적화된 런타임 환경
########################################################################################
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 헬스체크 용 curl 설치 (옵션)
RUN apk add --no-cache curl

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

########################################################################################
# 이미지 파일 처리 옵션 (둘 중 하나만 사용)
########################################################################################

# [옵션 1] static 폴더도 같이 복사 (로컬테스트, S3 미연결 환경)
COPY --from=builder /app/src/main/resources/static /app/static

# [옵션 2] S3에만 의존 (이미지 최소화, 프로덕션 권장)
# COPY --from=builder /app/src/main/resources/static /app/static

########################################################################################
# 환경 변수, 포트, ENTRYPOINT
########################################################################################

# 환경변수 예시 (실제 필요에 맞게 추가)
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
