package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.controller
 * FileName     : SseController
 * Author       : dounguk
 * Date         : 2025. 9. 3.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
        @RequestParam("receiverId") UUID receiverId,
        @RequestHeader(name = "Last-Event-ID", required = false) String lastEventId
    ) {
        UUID last = null;
        if (lastEventId != null && !lastEventId.isBlank()) {
            try {
                last = UUID.fromString(lastEventId.trim());
            } catch (IllegalArgumentException ignore) {
            }

        }
        return sseService.connect(receiverId, last);
    }
}
