package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> findAllByReceiverId(
            @AuthenticationPrincipal DiscodeitUserDetails principal) {
        UUID receiverId = principal.getUserDto().id();
        List<NotificationDto> notifications = notificationService.findAllByReceiverId(receiverId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notifications);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal DiscodeitUserDetails principal,
            @PathVariable UUID notificationId) {
        UUID receiverId = principal.getUserDto().id();
        notificationService.delete(notificationId, receiverId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
