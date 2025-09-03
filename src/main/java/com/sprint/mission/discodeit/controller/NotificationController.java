package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestHeader("Authorization") String bearerToken) {
        List<NotificationDto> list = notificationService.findAll(resolveToken(bearerToken));
        return ResponseEntity.ok().body(list);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId, @RequestHeader("Authorization") String bearerToken) {
        notificationService.deleteNotification(notificationId,resolveToken(bearerToken));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    private UUID resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return jwtTokenProvider.getUserId(bearerToken.substring(7));
        }
        return null;
    }

}
