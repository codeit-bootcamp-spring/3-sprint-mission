### 빌드 stage
#1 베이스 이미지 설정 : Amazon Corretto 17
FROM amazoncorretto:17 AS builder

#2 작업 디렉토리 설정
WORKDIR /app

#3 프로젝트 파일 컨테이너로 복사. 제외파일은 .dockignore로 처리
COPY . .

#4 GradleWrapper로 애플리케이션 빌드
RUN chmod +x ./gradlew && ./gradlew build --no-daemon -x test


## 실행 stage
#1 베이스 이미지
FROM amazoncorretto:17 AS runner
#2 노출포트 : 80
EXPOSE 80

#3 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""
ENV APP_JAR=/app/${PROJECT_NAME}-${PROJECT_VERSION}.jar

#4 빌드된 결과물만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ${APP_JAR}
#5 애플리케이션 실행 명령어 설정
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar ${APP_JAR}"]