package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @RequestHeader("Authorization") String accessToken
    ) {
        String token = accessToken.startsWith("Bearer ")
                ? accessToken.substring(7)
                : accessToken;

        UUID receiverId = jwtTokenProvider.getUserId(token);

        // 401 전역 예외처리
        if (receiverId == null) {
            throw new DiscodeitException("인증되지 않은 사용자입니다.", Instant.now(), ErrorCode.UNAUTHORIZED_USER, null);
        }

        List<NotificationDto> notificationList = notificationService.findByUserId(receiverId);

        return ResponseEntity.status(HttpStatus.OK).body(notificationList);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID notificationId,
            @RequestHeader("Authorization") String accessToken
    ) {
        if (accessToken == null) {
            throw new DiscodeitException("인증되지 않은 요청입니다.", Instant.now(), ErrorCode.UNAUTHORIZED_USER, null);
        }

        String token = accessToken.startsWith("Bearer ")
                ? accessToken.substring(7)
                : accessToken;

        UUID receiverId = jwtTokenProvider.getUserId(token);

        if (receiverId == null) {
            throw new DiscodeitException("인증되지 않은 요청입니다.", Instant.now(), ErrorCode.UNAUTHORIZED_USER, null);
        }

        notificationService.delete(notificationId, receiverId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
