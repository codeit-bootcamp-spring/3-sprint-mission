package com.sprint.mission.discodeit.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingInterceptor implements HandlerInterceptor {

  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    final String requestId = UUID.randomUUID().toString();
    MDC.put("request-id", requestId);
    MDC.put("http-method", request.getMethod());
    MDC.put("request-uri", request.getRequestURI());
    response.setHeader("Discodeit-Request-ID", requestId);

    return true;
  }

  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception e) {
    MDC.clear();
  }

}
