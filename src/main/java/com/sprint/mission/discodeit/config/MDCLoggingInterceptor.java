package com.sprint.mission.discodeit.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
public class MDCLoggingInterceptor implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String requestId = UUID.randomUUID().toString();

            MDC.put("requestId", requestId);
            MDC.put("requestMethod", request.getMethod());
            MDC.put("requestURI", request.getRequestURI());

            response.setHeader("Discodeit-Request_ID", requestId);

            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }
    }
}
