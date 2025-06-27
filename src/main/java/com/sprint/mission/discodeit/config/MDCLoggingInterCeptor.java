package com.sprint.mission.discodeit.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class MDCLoggingInterCeptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String requestMethod = request.getMethod();
        String requestURL = request.getRequestURI();

        // MDC에 컨텍스트 정보 설정
        MDC.put("RequestID", requestId);
        MDC.put("RequestMethod", requestMethod);
        MDC.put("RequestURL", requestURL);

        response.setHeader("Discodeit-Request-ID", requestId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {

        MDC.clear();
    }
}
