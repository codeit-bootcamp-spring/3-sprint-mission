# Amazon Corretto 17 이미지 사용
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사 (불필요한 건 .dockerignore로 제외)
COPY . .

# Gradle Wrapper를 사용하여 애플리케이션 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test


# 실행할 JAR 경로로 복사
RUN mkdir -p /app/build/libs

# 80 포트 노출
EXPOSE 80

# 프로젝트 정보 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV SPRING_PROFILES_ACTIVE=prod

# JVM 옵션 환경 변수 (빈 문자열로 기본 설정)
ENV JVM_OPTS=""

# 실행 스크립트 복사
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# 기존 CMD 제거하고 아래로 대체
CMD ["/app/entrypoint.sh"]