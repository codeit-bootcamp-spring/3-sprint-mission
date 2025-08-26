package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> findAll(Authentication authentication) {
    DiscodeitUserDetails details = (DiscodeitUserDetails) authentication.getPrincipal();
    var notifications = notificationService.findAllByReceiverId(details.getUser().id());
    var dtos = notifications.stream().map(NotificationDto::from).toList();
    return ResponseEntity.ok(dtos);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID notificationId,
      Authentication authentication) {
    DiscodeitUserDetails details = (DiscodeitUserDetails) authentication.getPrincipal();
    notificationService.delete(notificationId, details.getUser().id());
    return ResponseEntity.noContent().build();
  }
}
