package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
        @AuthenticationPrincipal DiscodeitUserDetails userDetails,
        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

        UUID receiverId = userDetails.getUserId();
        UUID lastEventUuid = lastEventId != null ? UUID.fromString(lastEventId) : null;


        log.info("SSE 연결 요청 : receiverId = {}, lastEventUuid = {}", receiverId, lastEventUuid);

        return sseService.connect(receiverId, lastEventUuid);
    }
}