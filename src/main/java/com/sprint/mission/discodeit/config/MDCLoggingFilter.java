package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * {@code MDCLoggingFilter}는 각 HTTP 요청마다 고유한 요청 ID를 생성하여
 * SLF4J의 MDC(Mapped Diagnostic Context)에 기록하고, 해당 ID를 응답 헤더에도 포함시켜
 * 클라이언트 및 로그 추적 시 활용할 수 있도록 하는 설정 클래스입니다.
 *
 * <p>이 설정은 다음을 수행합니다:
 * <ul>
 *     <li>요청마다 고유한 {@code traceId}를 생성</li>
 *     <li>{@code MDC}에 traceId, URI, method, IP 정보를 기록</li>
 *     <li>응답 헤더에 {@code Discodeit-Request-ID}로 traceId를 포함</li>
 *     <li>요청 처리 완료 후 MDC를 정리</li>
 * </ul>
 *
 */
@Configuration
public class MDCLoggingFilter{

    /**
     * 요청마다 traceId를 생성하고 MDC에 기록하며, 응답 헤더에 추가하는 필터를 정의합니다.
     *
     * @return {@link OncePerRequestFilter} Bean 등록 객체
     */
    @Bean
    public OncePerRequestFilter mdcLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                // 고유한 요청 ID (traceId) 생성 – 앞의 8자리만 사용하여 간결화
                String traceId = UUID.randomUUID().toString().substring(0, 8);

                try {
                    // MDC에 요청 정보 등록
                    MDC.put("traceId", traceId);
                    MDC.put("requestURI", request.getRequestURI());
                    MDC.put("method", request.getMethod());
                    MDC.put("url", request.getRequestURL().toString());
                    MDC.put("remoteAddr", getClientIpAddress(request));

                    // 응답 헤더에 traceId 포함 – 클라이언트에서 요청 추적 가능
                    response.setHeader("Discodeit-Request-ID", traceId);

                    // 다음 필터 체인으로 요청 전달
                    filterChain.doFilter(request, response);
                } finally {
                    // 요청 처리 완료 후 MDC 정리 (메모리 누수 방지)
                    MDC.clear();
                }
            }
        };
    }

    /**
     * 클라이언트의 실제 IP 주소를 추출합니다.
     * <p>X-Forwarded-For 또는 X-Real-IP 헤더를 우선적으로 사용하고,
     * 없으면 기본 요청의 remote address를 반환합니다.
     *
     * @param request 현재 HTTP 요청 객체
     * @return 클라이언트 IP 주소 문자열
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("x-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim(); // 첫 번째 IP가 원래 클라이언트
        }

        String xRealIp = request.getHeader("x-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp.split(",")[0].trim();
        }

        return request.getRemoteAddr(); // 기본 fallback
    }
}
