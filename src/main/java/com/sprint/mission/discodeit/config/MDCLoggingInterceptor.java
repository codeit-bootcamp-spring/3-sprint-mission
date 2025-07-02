package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class MDCLoggingInterceptor implements HandlerInterceptor {

    private static final String REQ_ID_KEY = "requestId";
    private static final String REQ_URL_KEY = "requestUrl";
    private static final String REQ_MTD_KEY = "requestMethod";
    private static final String RESPONSE_HEADER = "Discodeit-Request-ID";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler) {

        String requestId = UUID.randomUUID().toString();

        MDC.put(REQ_ID_KEY, requestId);
        MDC.put(REQ_URL_KEY, request.getRequestURI());
        MDC.put(REQ_MTD_KEY, request.getMethod());

        response.setHeader(RESPONSE_HEADER, requestId);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        Exception ex) {

        MDC.clear();
    }
}

