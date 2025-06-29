package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.UUID;

@Configuration
public class MDCLoggingInterceptor implements HandlerInterceptor {

    @Bean
    public OncePerRequestFilter mdcLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                // 요청별 고유 ID 생성
                String traceId = UUID.randomUUID().toString().substring(0, 8);

                try {
                    // MDC에 traceId 설정
                    MDC.put("traceId", traceId);
                    MDC.put("requestURI", request.getRequestURI());
                    MDC.put("method", request.getMethod());
                    MDC.put("remoteAddr", getClientIpAddress(request));

                    response.setHeader("Discodeit-Request-ID", traceId);

                    // 다음 필터 체인 실행
                    filterChain.doFilter(request, response);
                } finally {
                    // 요청 처리 완료 후 MDC 정리
                    MDC.clear();
                }
            }
        };
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("x-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("x-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.split(",")[0].trim();
        }

        return request.getRemoteAddr(); // fallback
    }
}
