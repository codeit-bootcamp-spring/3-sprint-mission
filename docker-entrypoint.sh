#!/bin/sh
exec java $JVM_OPTS -jar ${PROJECT_NAME}-${PROJECT_VERSION}.jar "$@"
