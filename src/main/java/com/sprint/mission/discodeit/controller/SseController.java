package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.SseApi;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController implements SseApi {

  private final SseService sseService;

  @Override
  @GetMapping
  public ResponseEntity<SseEmitter> connect(
      @RequestParam UUID receiverId,
      @RequestParam(required = false) UUID lastEventId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader
  ) {
    UUID resumeId = lastEventId;
    if (resumeId == null && lastEventIdHeader != null && !lastEventIdHeader.isBlank()) {
      try {
        resumeId = UUID.fromString(lastEventIdHeader.trim());
      } catch (IllegalArgumentException ignored) {
      }
    }
    SseEmitter emitter = sseService.connect(receiverId, resumeId);
    return ResponseEntity.ok(emitter);
  }
}
