package com.sprint.mission.discodeit.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();

        return () -> {
            try{
                SecurityContextHolder.setContext(securityContext);
                MDC.setContextMap(copyOfContextMap);
                runnable.run();
            }finally {
                SecurityContextHolder.clearContext();
                MDC.clear();
            }
        };
    }
}
