package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.SseApi;
import com.sprint.mission.discodeit.exception.auth.InvalidTokenException;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
      @RequestParam(required = false) UUID lastEventId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new InvalidTokenException("인증된 사용자 정보가 없습니다.");
    }
    Object principal = authentication.getPrincipal();
    UUID receiverId = null;
    if (principal instanceof DiscodeitUserDetails userDetails) {
      receiverId = userDetails.getUser().id();
    }
    if (receiverId == null) {
      throw new InvalidTokenException("JWT에서 receiverId(userId)를 추출할 수 없습니다.");
    }
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
