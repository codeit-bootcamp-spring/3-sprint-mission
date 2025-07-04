
# ecr 로그인
#aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws


# 멀티플랫폼 이미지 빌드 - regular
#docker buildx build \
#  --platform linux/amd64,linux/arm64 \
#  -t public.ecr.aws/m8n4t4p5/discodeit:latest \
#  -t public.ecr.aws/m8n4t4p5/discodeit:1.2-M8 \
#  --push .

# slim
#docker buildx build \
#  --platform linux/amd64 \
#  -t public.ecr.aws/m8n4t4p5/discodeit:local-slim \
#  -t public.ecr.aws/m8n4t4p5/discodeit:latest \
#  --push .


########################################################################################
# 1단계: Gradle 빌드 환경
########################################################################################
#FROM amazoncorretto:17 AS builder
#
#WORKDIR /app
#
#RUN yum update -y && \
#    yum install -y curl && \
#    yum clean all
#
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY settings.gradle .
#COPY src src
#
#RUN ./gradlew bootJar
#
########################################################################################
# 2단계: 실행 환경
########################################################################################
#FROM amazoncorretto:17
#
#WORKDIR /app
#
## 환경 변수 설정
#ENV PROJECT_NAME=discodeit
#ENV PROJECT_VERSION=1.2-M8
#ENV JVM_OPTS=""
#
## 실행할 JAR 파일 복사
#COPY --from=builder /app/build/libs/*.jar app.jar
#
## 정적 파일 복사 (옵션 1: 포함)
#COPY --from=builder /app/src/main/resources/static /app/static
## 정적 파일 복사 (옵션 2: 완전한 S3 의존)
## COPY --from=builder /app/src/main/resources/static /app/static
#
## 80 포트 노출
#EXPOSE 80
#
## 실행 명령 설정 - 환경 변수 활용
#ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]



# 경량화
# ------------------------------------------------------------
# 1단계: Gradle 빌드 환경
# ------------------------------------------------------------
FROM gradle:jdk17-alpine AS builder

WORKDIR /app
COPY . .

RUN gradle bootJar --no-daemon

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
