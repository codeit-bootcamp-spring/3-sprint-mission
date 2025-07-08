# --------------------------
# 빌드 스테이지
# --------------------------
FROM amazoncorretto:17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies

COPY src ./src
RUN ./gradlew build -x test


# --------------------------
# 런타임 스테이지
# --------------------------
FROM amazoncorretto:17-alpine3.21

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 빌드한 jar 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./

# 엔트리포인트 스크립트 복사
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# 컨테이너 내부 포트 노출
EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar"]