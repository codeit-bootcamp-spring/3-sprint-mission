package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
    private static final String REQUEST_ID = "requestId";
    private static final String METHOD = "method";
    private static final String URI = "uri";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");

        MDC.put(REQUEST_ID, requestId);
        MDC.put(METHOD, request.getMethod());
        MDC.put(URI, request.getRequestURI());

        // 응답 헤더에 요청 ID 추가
        response.setHeader(REQUEST_ID_HEADER, requestId);

        log.debug("Request started");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) {
        log.debug("Request completed");
        MDC.clear();
    }
}
