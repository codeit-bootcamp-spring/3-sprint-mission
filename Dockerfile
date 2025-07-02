# -------------------------------------------------------------------
# 1단계: 빌드 스테이지 (빌드 도구 포함)
# Amazon Corretto 17 베이스 이미지 사용
FROM amazoncorretto:17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보를 환경 변수로 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

# JVM 옵션을 환경 변수로 설정
ENV JVM_OPTS=""

# Gradle Wrapper와 설정 파일들을 먼저 복사 (Docker 레이어 캐싱 최적화)
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle/ gradle/

# 의존성만 먼저 다운로드 (캐시 활용)
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || return 0

# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew build -x test --no-daemon

# -------------------------------------------------------------------
# 2단계: 런타임 스테이지 (경량 이미지)
FROM amazoncorretto:17-alpine AS runtime

# 작업 디렉토리 설정
WORKDIR /app

# curl 설치
RUN apk add --no-cache curl

# 프로젝트 정보를 환경 변수로 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 빌드 결과물만 복사 (최종 JAR 파일만 포함)
COPY --from=build /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./app.jar

# 기본 포트 노출 (실제 포트는 SERVER_PORT 환경변수로)
EXPOSE 80

# 애플리케이션 실행을 위한 ENTRYPOINT와 CMD 조합
ENTRYPOINT ["sh", "-c"]
CMD ["java ${JVM_OPTS} -jar app.jar"]
