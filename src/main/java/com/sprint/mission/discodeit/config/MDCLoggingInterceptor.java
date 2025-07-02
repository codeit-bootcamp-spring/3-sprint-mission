package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString();

        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("url", request.getRequestURI());

        response.setHeader("Discodeit-Request-ID", requestId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        // 요청 처리 후 MDC 값 제거
        MDC.remove("requestId");
        MDC.remove("method");
        MDC.remove("url");
    }
}
