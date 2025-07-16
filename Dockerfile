# A. 빌드 스테이지
# 1. 베이스 이미지 선택
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
# 컨테이너 내부에서 모든 작업이 수행될 기본 디렉토리
# /app 디렉토리가 없으면 자동으로 생성된다
WORKDIR /app

# Gradle 래퍼와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (캐시 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew build --no-daemon

# B. 실행 스테이지
FROM amazoncorretto:17
WORKDIR /app

# 빌드 스테이지에서 JAR 파일만 복사
# 3. JAR 파일 복사
# build/libs/*.jar - Gradle 빌드 결과물 (와일드카드로 버전 무관하게 복사)
# app.jar - 컨테이너 내부에서 사용할 파일명 (단순화)
COPY build/libs/discodeit-0.0.1-SNAPSHOT.jar app.jar

# 4. 포트 노출 설정
# EXPOSE는 문서화 목적이며, 실제 포트 매핑은 docker run -p 옵션으로 설정
EXPOSE 80

# 5. 환경 변수 설정
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 6. 컨테이너 시작 시 실행할 명령어
# java -jar app.jar - Spring Boot 애플리케이션 실행
# CMD는 컨테이너 실행 시 기본 명령어 (docker run에서 오버라이드 가능)
CMD ["java", "-jar", "app.jar"]

# ===================================================================
# 빌드 및 실행 예시:
# 
# 1. 애플리케이션 빌드:
# ./gradlew build
# 
# 2. Docker 이미지 빌드:
# docker build -t discodeit-app:local .
#
# 멀티 플랫폼 이미지 빌드
# docker buildx build \
# --platform linux/amd64,linux/arm64 \
# -t discodeit-app:latest .
# 
# 3. 컨테이너 실행:
# docker run -d --name discodeit-app-local \
#  -p 8081:80 \
#  -e SPRING_PROFILES_ACTIVE=prod \
#  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/discodeit \
#  -e SPRING_DATASOURCE_USERNAME=discodeit_user \
#  -e SPRING_DATASOURCE_PASSWORD=discodeit1234 \
#  discodeit-app:latest
# 
# 4. 애플리케이션 접속 확인:
# curl http://localhost:8888/actuator/health
# =================================================================== 