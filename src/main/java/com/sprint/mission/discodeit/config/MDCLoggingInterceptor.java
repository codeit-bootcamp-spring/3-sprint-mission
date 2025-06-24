package com.sprint.mission.discodeit.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.config
 * FileName     : MDCLoggingInterceptor
 * Author       : dounguk
 * Date         : 2025. 6. 24.
 */
@Configuration
public class MDCLoggingInterceptor implements HandlerInterceptor {

    public static final String TRACE_ID = "traceId";
    public static final String METHOD = "method";
    public static final String REQUEST_URI = "requestURI";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(TRACE_ID, traceId);
        MDC.put(METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());

        response.setHeader("Discodeit-Request-ID", traceId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
