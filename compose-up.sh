#!/bin/bash
set -a
source .env.docker
set +a
# .env.docker 파일에 있는 환경변수를 로드하여 docker-compose up 명령어 실행
docker compose -f docker-compose.yaml up --build -d
