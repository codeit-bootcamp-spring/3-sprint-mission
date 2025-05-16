package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-status")
public class UserStatusController {

    private final UserStatusService userStatusService;

    @PostMapping
    public ResponseEntity<UserStatus> create(@RequestBody UserStatusCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userStatusService.create(request));
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserStatus> find(@PathVariable("userId") UUID id) {
        return ResponseEntity.ok(userStatusService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<UserStatus>> findAll() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    @PatchMapping("/api/users/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(userStatusService.update(userId, request));
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID id) {
        userStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
