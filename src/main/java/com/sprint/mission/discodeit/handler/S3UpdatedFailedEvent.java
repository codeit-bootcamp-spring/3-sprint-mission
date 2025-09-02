package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import lombok.Getter;
import org.jboss.logging.MDC;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : S3UpdatedFailedEvent
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
@Getter
public class S3UpdatedFailedEvent {
    private final UUID id;
    private final Throwable throwable;
    private final String requestId;

    public S3UpdatedFailedEvent(UUID id, Throwable throwable) {
        this.id = id;
        this.throwable = throwable;
        this.requestId = MDC.get(MDCLoggingInterceptor.REQUEST_ID).toString();

    }
}
