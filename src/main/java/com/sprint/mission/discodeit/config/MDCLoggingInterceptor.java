package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String METHOD_KEY = "method";
    private static final String URI_KEY = "uri";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8); //요구사항의 예시가 8자리
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(METHOD_KEY, request.getMethod());
        MDC.put(URI_KEY, request.getRequestURI());

        response.setHeader("Discodeit-Request-ID", requestId); // 응답 헤더에 추가

        return true;
    }

    //요청 후 실행 MDC 비우기
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) {
        MDC.clear();
    }
}