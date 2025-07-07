# Multi-stage build를 사용하여 이미지 크기 최적화
# Stage 1: 빌드 스테이지
FROM amazoncorretto:17 AS build

# 작업 디렉토리를 설정하세요. (/app)
WORKDIR /app

# Gradle 관련 파일들을 먼저 복사 (레이어 캐싱 최적화)
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Gradle wrapper에 실행 권한 부여
RUN chmod +x gradlew


# 소스 코드 복사
COPY src/ src/

# 애플리케이션 빌드 (테스트 건너뛰기, 데몬 비활성화로 메모리 절약)
RUN ./gradlew build -x test --no-daemon

# Stage 2: 실행 스테이지 (경량 이미지)
FROM amazoncorretto:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보를 환경 변수로 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 빌드 스테이지에서 JAR 파일만 복사
COPY --from=build /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 80 포트 노출
EXPOSE 80

# 애플리케이션 실행
# 시작 스크립트 생성 (따옴표 제거 처리 포함)
RUN echo '#!/bin/sh' > /start.sh && \
    echo 'echo "=== Environment Variables ==="' >> /start.sh && \
    echo 'env | grep -E "(JVM_OPTS|JAVA_OPTS)"' >> /start.sh && \
    echo 'echo "============================="' >> /start.sh && \
    echo '' >> /start.sh && \
    echo '# JVM_OPTS에서 따옴표 제거' >> /start.sh && \
    echo 'if [ -n "$JVM_OPTS" ]; then' >> /start.sh && \
    echo '  # 앞뒤 따옴표 제거' >> /start.sh && \
    echo '  JVM_OPTS=$(echo "$JVM_OPTS" | sed "s/^\"\(.*\)\"$/\1/")' >> /start.sh && \
    echo '  echo "Cleaned JVM_OPTS: $JVM_OPTS"' >> /start.sh && \
    echo 'else' >> /start.sh && \
    echo '  JVM_OPTS="-Xmx512m -Xms256m"' >> /start.sh && \
    echo '  echo "Using default JVM_OPTS: $JVM_OPTS"' >> /start.sh && \
    echo 'fi' >> /start.sh && \
    echo '' >> /start.sh && \
    echo 'echo "Final command: java $JVM_OPTS -jar /app/app.jar"' >> /start.sh && \
    echo 'exec java $JVM_OPTS -jar /app/app.jar' >> /start.sh && \
    chmod +x /start.sh

ENTRYPOINT ["/start.sh"]