#!/bin/bash

./image-build.sh || { echo "이미지 빌드 실패"; exit 1; }
./compose-up.sh || { echo "docker-compose up 실패"; exit 1; }