FROM amazoncorretto:17

WORKDIR /app

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

COPY build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 80