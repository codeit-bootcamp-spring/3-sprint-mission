#!/bin/bash
set -a
source discodeit.env
set +a
# .env.docker 파일에 있는 환경변수를 로드하여 멀티플랫폼 docker image build(2가지 태그 만듦)
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --build-arg PROJECT_NAME="$PROJECT_NAME" \
  --build-arg PROJECT_VERSION="$PROJECT_VERSION" \
  --build-arg JVM_OPTS="$JVM_OPTS" \
  -t "$PROJECT_NAME":"$PROJECT_VERSION" \
  -t "$PROJECT_NAME":latest \
    .

#docker buildx build \
#  --progress=plain \
#  --no-cache \
#  --platform linux/amd64,linux/arm64 \
#  --build-arg PROJECT_NAME="$PROJECT_NAME" \
#  --build-arg PROJECT_VERSION="$PROJECT_VERSION" \
#  --build-arg JVM_OPTS="$JVM_OPTS" \
#  -t "$PROJECT_NAME":"$PROJECT_VERSION" \
#  -t "$PROJECT_NAME":latest \

# 위에서 생성한 이미지(2가지 태그)를 AWS ECR용 태그 붙임
docker tag "$PROJECT_NAME":"$PROJECT_VERSION" 814943008023.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit:"$PROJECT_VERSION"
docker tag "$PROJECT_NAME":latest 814943008023.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit:latest

#  AWS ECR에 push
docker push 814943008023.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit:"$PROJECT_VERSION"
docker push 814943008023.dkr.ecr.ap-northeast-2.amazonaws.com/discodeit:latest