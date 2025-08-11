package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 요청별 MDC 로깅 인터셉터
 */
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String TRACE_ID = "traceId";
  private static final String REQUEST_METHOD = "requestMethod";
  private static final String REQUEST_URL = "requestUrl";
  private static final String HDR_TRACE_ID = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    String traceId = headerOrNull(request, HDR_TRACE_ID);
    if (traceId == null) {
      traceId = UUID.randomUUID().toString();
    }

    MDC.put(TRACE_ID, traceId);
    MDC.put(REQUEST_METHOD, request.getMethod());
    MDC.put(REQUEST_URL, request.getRequestURI());
    response.setHeader(HDR_TRACE_ID, traceId);
    return true;
  }

  @Override
  public void afterCompletion(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler, @Nullable Exception ex) {
    MDC.clear();
  }

  private String headerOrNull(HttpServletRequest request, String name) {
    String v = request.getHeader(name);
    return (v == null || v.isBlank()) ? null : v;
  }
}
