package com.sprint.mission.discodeit.mdc.decorator;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MDCTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        return () -> {
            try {
                // 비동기 스레드에서 컨텍스트 복원
                SecurityContextHolder.setContext(securityContext);

                if (mdcContext != null) {
                    MDC.setContextMap(mdcContext);
                }

                // 원본 작업 실행
                runnable.run();
            } finally {
                SecurityContextHolder.clearContext();
                MDC.clear();
            }
        };
    }
}
