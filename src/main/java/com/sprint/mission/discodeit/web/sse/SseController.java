package com.sprint.mission.discodeit.web.sse;

import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RequestMapping(value = "/api")
@RestController
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @AuthenticationPrincipal DiscodeitUserDetails principal,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader,
            @RequestParam(value = "lastEventId", required = false) UUID lastEventIdParam
    ) {
        UUID receiverId = principal.getUserDto().id();
        UUID lastEventId = null;
        if (lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
            try { lastEventId = UUID.fromString(lastEventIdHeader); } catch (Exception ignore) {}
        }
        if (lastEventId == null) lastEventId = lastEventIdParam;
        return sseService.connect(receiverId, lastEventId);
    }
}
