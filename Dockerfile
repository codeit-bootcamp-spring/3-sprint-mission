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
# 소스 코드 복사 되어있는지 확인
RUN echo "=== /app 파일 리스트 ===" && ls -alh /app
RUN ./gradlew bootJar
# build/libs 안에 어떤 파일이 생겼는지 로그 확인
RUN echo "=== JAR 파일 리스트 (builder 스테이지) ===" && ls -alh /app/build/libs
# 멀티스테이지 2단계: runtime
# 1. 베이스 이미지 선택
FROM amazoncorretto:17
# 2. 환경변수(Build-time 변수, 이미지에 저장 안됨)
ARG PROJECT_NAME
ARG PROJECT_VERSION
ARG JVM_OPTS
# 2. 환경변수(Runtime 변수,컨테이너 실행 시, 이미지에 저장됨)
ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}
ENV JVM_OPTS=${JVM_OPTS}
# 2. 전체 환경변수 목록 확인
RUN echo "PROJECT_NAME during build: $PROJECT_NAME"
RUN echo "PROJECT_VERSION during build: $PROJECT_VERSION"
RUN echo "JVM_OPTS during build: $JVM_OPTS"
# 3. 작업 디렉토리 설정
WORKDIR /app
# 4.빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar
# 파일 복사 잘 됐는지 확인
RUN echo "=== JAR 복사 후 확인 (runtime 스테이지) ===" && ls -alh /app
# 5. 포트 노출
EXPOSE 80
# 6. DB 실행까지 기다리는 스크립트
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# 7. 애플리케이션 실행 명령
CMD sh -c "/wait-for-it.sh postgresDB:5432 -- echo 'postgresDB is up' && java $JVM_OPTS -jar /app/${PROJECT_NAME}-${PROJECT_VERSION}.jar"
