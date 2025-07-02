# 멀티스테이지 1단계: builder
# 1. 베이스 이미지 선택
FROM amazoncorretto:17 AS builder
# 2. 작업 디렉토리 설정
WORKDIR /app
# 3. Gradle Wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN ./gradlew bootJar

# 멀티스테이지 2단계: runtime
# 1. 베이스 이미지 선택
FROM amazoncorretto:17
# 2. 환경변수(Build-time 변수, 이미지에 저장 안됨)
ARG PROJECT_NAME
ARG PROJECT_VERSION
ARG JVM_OPTS=""
# 2. 환경변수(Runtime 변수,컨테이너 실행 시, 이미지에 저장됨)
ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}
ENV JVM_OPTS=${JVM_OPTS}
# 2. 전체 환경변수 목록 확인

# 3. 작업 디렉토리 설정
WORKDIR /app
# 4.빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar
# 5. 포트 노출
EXPOSE 8080
# 6. 애플리케이션 실행 명령
CMD java $JVM_OPTS -jar "$PROJECT_NAME-$PROJECT_VERSION.jar"
