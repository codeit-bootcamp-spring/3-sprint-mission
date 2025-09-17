package com.sprint.mission.discodeit.sse;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    // 표준: Last-Event-ID 헤더 사용 (없으면 null 허용)
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
        @RequestParam(required = false) UUID receiverId,
        @RequestHeader(name = "Last-Event-ID", required = false) String lastEventIdHeader
    ) {
        if (receiverId == null) {
            // TODO: 보안 컨텍스트에서 현재 사용자 ID를 추출해 사용하고 싶으면 여기에 적용
            // ex) receiverId = currentUserIdProvider.get();
            throw new IllegalArgumentException("receiverId is required (또는 인증 사용자 ID 사용으로 교체)");
        }
        UUID lastEventId = null;
        if (lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
            try { lastEventId = UUID.fromString(lastEventIdHeader); } catch (IllegalArgumentException ignore) {}
        }
        return sseService.connect(receiverId, lastEventId);
    }
}