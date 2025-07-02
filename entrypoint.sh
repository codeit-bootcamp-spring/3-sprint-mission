#!/bin/sh

echo "► JVM_OPTS: $JVM_OPTS"
echo "► 실행 대상 JAR: build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"

exec java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar