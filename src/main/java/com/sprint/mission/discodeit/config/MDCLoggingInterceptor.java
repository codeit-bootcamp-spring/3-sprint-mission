package com.sprint.mission.discodeit.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 요청별 MDC 로깅 인터셉터
 */
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {
  private static final String REQUEST_ID = "requestId";
  private static final String REQUEST_METHOD = "requestMethod";
  private static final String REQUEST_URL = "requestUrl";
  private static final String HEADER_NAME = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String requestId = UUID.randomUUID().toString();
    MDC.put(REQUEST_ID, requestId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URL, request.getRequestURI());
    response.setHeader(HEADER_NAME, requestId);
    return true;
  }

  @Override
  public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler, @Nullable Exception ex) {
    MDC.clear();
  }
}
