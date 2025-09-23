package com.sprint.mission.discodeit.util;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            try {
                if (contextMap != null) {
                    // 새로운 스레드에 MDC 값 복사
                    MDC.setContextMap(contextMap);
                }
                // 기존 비동기 작업 실행
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
