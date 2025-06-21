package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 로깅 관련 설정을 담당하는 Configuration 클래스
 *
 * <p>MDC(Mapped Diagnostic Context)를 활용한 요청 추적 설정과
 * 로깅 필터를 구성한다.</p>
 */
@Configuration
public class LoggingConfig {

  /**
   * 요청별 고유 ID를 생성하고 MDC에 설정하는 필터를 등록한다.
   *
   * <p>각 HTTP 요청마다 고유한 traceId를 생성하여 MDC에 저장하고,
   * 요청 처리가 완료되면 MDC를 정리한다.</p>
   *
   * @return MDC 설정 필터
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
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

          // 다음 필터 체인 실행
          filterChain.doFilter(request, response);

        } finally {
          // 요청 처리 완료 후 MDC 정리
          MDC.clear();
        }
      }
    };
  }

  /**
   * 클라이언트의 실제 IP 주소를 추출한다.
   *
   * <p>프록시나 로드밸런서를 거쳐온 요청의 경우
   * X-Forwarded-For 헤더에서 실제 IP를 추출한다.</p>
   *
   * @param request HTTP 요청
   * @return 클라이언트 IP 주소
   */
  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
} 