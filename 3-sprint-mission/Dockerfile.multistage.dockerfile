# ===================================================================
# Dockerfile.multistage - 멀티 스테이지 빌드를 활용한 이미지 최적화
# ===================================================================
# 이 Dockerfile은 빌드 환경과 런타임 환경을 분리하여 최종 이미지 크기를
# 최적화하는 방법을 보여준다. 프로덕션 환경에서 권장되는 방식이다.

# ===================================================================
# 1단계: 빌드 스테이지 (Build Stage)
# ===================================================================
# 빌드 도구(Gradle)와 소스 코드를 이용해 JAR 파일을 생성하는 단계
FROM eclipse-temurin:17-jdk AS builder
# 이건 amd64 기반으로 작성되었을 것.
# 근데 M1, M2 CPU는 arm64 기반임.. 그래서 안 될 거다.

# 빌드 스테이지 메타데이터
LABEL stage="builder" \
      description="Application build stage"

# 빌드 작업 디렉토리 설정
WORKDIR /app

# 1-1. Gradle 설정 파일들을 먼저 복사 (Docker 레이어 캐싱 최적화)
# 의존성 파일들이 변경되지 않으면 이 레이어는 재사용된다
COPY gradle ./gradle
COPY gradlew ./
COPY *.gradle ./
COPY gradle.properties* ./

# 1-2. Gradle 래퍼 실행 권한 부여
# Linux 환경에서 Gradle 래퍼 스크립트 실행을 위해 필요
RUN chmod +x gradlew

# 1-3. 의존성 다운로드 (별도 단계로 분리하여 캐싱 효과 극대화)
# 소스 코드가 변경되어도 의존성은 다시 다운로드하지 않음
RUN ./gradlew dependencies --no-daemon --quiet

# 1-4. 소스 코드 복사 (변경이 가장 빈번한 파일들을 마지막에 복사)
COPY src ./src

# 1-5. 애플리케이션 빌드 실행
# -x test: 테스트 생략으로 빌드 시간 단축 (CI/CD에서 별도 실행)
# --no-daemon: Docker 환경에서 Gradle 데몬 비활성화
RUN ./gradlew build --no-daemon -x test --quiet

# ===================================================================
# 2단계: 런타임 스테이지 (Runtime Stage)
# ===================================================================
# 실제로 배포될 최종 이미지. 빌드 도구와 소스 코드는 포함하지 않음
FROM eclipse-temurin:17-jre

# 런타임 메타데이터
LABEL maintainer="discodeit" \
      description="Spring Boot Discode Application - Multi-stage Build" \
      stage="runtime" \
      version="1.0.0"

# 런타임 작업 디렉토리 설정
WORKDIR /app

# 2-1. 빌드 스테이지에서 생성된 JAR 파일만 복사
# --from=builder: 이전 빌드 스테이지에서 파일 복사
# 소스 코드, 빌드 도구 등은 제외하여 이미지 크기 최소화
COPY --from=builder /app/build/libs/*.jar app.jar

# 2-2. 포트 노출
EXPOSE 80

# 2-3. 애플리케이션 실행 명령어
# JRE만 사용하여 메모리 사용량 최적화
CMD ["java", "-jar", "app.jar"]

# ===================================================================
# 멀티 스테이지 빌드의 장점:
# 
# 1. 이미지 크기 최적화: 빌드 도구, 소스 코드 제외
# 2. 보안 강화: 런타임에 불필요한 도구 제거
# 3. 레이어 캐싱: 의존성 변경 시에만 해당 레이어 재빌드
# 4. 깨끗한 런타임 환경: 빌드 아티팩트만 포함
# 
# 빌드 및 실행 예제:
# 
# 1. 멀티 스테이지 빌드:
# docker build -f Dockerfile.multistage -t discodeit-app:optimized .
# 
# 2. 이미지 크기 비교:
# docker images | grep discodeit-app
# 
# 3. 최적화된 컨테이너 실행:
# docker run -p 8888:8080 discodeit-app:optimized
# =================================================================== 