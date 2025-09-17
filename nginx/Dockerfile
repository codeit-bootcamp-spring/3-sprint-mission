FROM nginx:alpine

# Nginx 설정 파일 복사
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# 빌드 컨텍스트가 프로젝트 루트이므로 상대경로로
COPY src/main/resources/static/ /usr/share/nginx/html/

EXPOSE 80