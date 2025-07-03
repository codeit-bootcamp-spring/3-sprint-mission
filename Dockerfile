# 1. 베이스 이미지 선택
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
# 컨테이너 내부에서 모든 작업이 수행될 기본 디렉토리
# /app 디렉토리가 없으면 자동으로 생성된다
WORKDIR /app

# 3. JAR 파일 복사
# build/libs/*.jar - Gradle 빌드 결과물 (와일드카드로 버전 무관하게 복사)
# app.jar - 컨테이너 내부에서 사용할 파일명 (단순화)
COPY build/libs/*.jar app.jar

# 4. 포트 노출 설정
# Spring Boot 애플리케이션의 기본 포트인 80을 노출
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
# docker build -t menu-app:basic .
# 
# 3. 컨테이너 실행:
# docker run -p 8888:8080 menu-app:basic
# 
# 4. 애플리케이션 접속 확인:
# curl http://localhost:8888/actuator/health
# =================================================================== 