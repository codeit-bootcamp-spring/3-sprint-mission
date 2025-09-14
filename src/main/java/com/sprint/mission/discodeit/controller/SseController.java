package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

        UUID receiverId = principal.getId();

        UUID lastEventId = null;

        if (lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
            try {
                lastEventId = UUID.fromString(lastEventIdHeader);
            } catch (IllegalArgumentException ignore) {
                log.warn("[SseController] 변환할 수 없는 형식 - {}", lastEventIdHeader);
            }
        }

        log.debug("[SseController] Sse 연결 요청 - receiverId: {} lastEventId: {}", receiverId,
            lastEventId);

        return sseService.connect(receiverId, lastEventId);
    }
}
