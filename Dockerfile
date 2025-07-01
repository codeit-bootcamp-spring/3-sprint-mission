# Amazon Corretto 17 베이스 이미지 사용
FROM amazoncorretto:17

# 환경 변수 설정
ENV PROJECT_NAME=3-sprint-mission
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 작업 디렉토리 설정
WORKDIR /app

# .dockerignore를 활용해 불필요한 파일 제외하고 복사
COPY . .

# 애플케이션 빌드
RUN chmod +x gradlew
RUN ./gradlew bootJar

# 80 포트 노출
EXPOSE 80

# 애플리케이션 실행
CMD ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
