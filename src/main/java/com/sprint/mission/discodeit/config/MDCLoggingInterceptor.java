package com.sprint.mission.discodeit.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCLoggingInterceptor implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        try {

            MDC.put("requestId", requestId);
            MDC.put("requestMethod", requestMethod);
            MDC.put("requestURI", requestURI);

            response.setHeader("Discodeit-Request-ID", requestId);

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            MDC.clear();
        }
    }
}
