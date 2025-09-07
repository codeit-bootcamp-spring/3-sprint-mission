FROM nginx:alpine

# 기본 제공 default.conf 제거(충돌 방지)
RUN rm /etc/nginx/conf.d/default.conf

# 우리의 리버스 프록시 설정 복사(80 포트에서 동작)
COPY nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf

# 정적 리소스(프론트엔드 빌드 산출물) 복사
# 프로젝트 내 src/main/resources/static 을 컨테이너 표준 경로로 복사
COPY src/main/resources/static /usr/share/nginx/html

# 정적/인증서 디렉토리 보강(필요 시)
RUN mkdir -p /usr/share/nginx/html/static && \
    mkdir -p /etc/nginx/certs

# 외부 노출은 80만 사용(Compose에서 3000:80)
EXPOSE 80