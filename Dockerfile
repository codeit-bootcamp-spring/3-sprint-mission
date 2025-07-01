# Amazon Corretto 17 베이스 이미지 사용
FROM amazoncorretto:17

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

# 소스 코드 복사
COPY src/ src/

# Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

# 애플리케이션 빌드
RUN ./gradlew build -x test

# 기본 포트 노출 (실제 포트는 SERVER_PORT 환경변수로)
EXPOSE 80

# 애플리케이션 실행을 위한 ENTRYPOINT와 CMD 조합
ENTRYPOINT ["sh", "-c"]
CMD ["java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
