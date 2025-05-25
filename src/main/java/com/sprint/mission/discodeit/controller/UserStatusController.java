package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/userStatus")
@Controller
public class UserStatusController {

    private final UserStatusService userStatusService;
    private final UserService userService;

    @RequestMapping(path = "/{userId}", method = RequestMethod.PATCH, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateUserOnlineStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request // UserStatusUpdateRequest 사용
    ) {
        try {
            // 1. UserStatusService를 사용하여 사용자의 마지막 활성 시간 업데이트
            userStatusService.updateByUserId(userId, request);

            // 2. UserService를 사용하여 업데이트된 사용자 정보(UserDto) 조회
            UserDto updatedUserDto = userService.getUserById(userId);

            return ResponseEntity.ok(updatedUserDto);
        } catch (NoSuchElementException e) {
            // userStatusService.updateByUserId 또는 userService.getUserById에서 사용자를 찾지 못한 경우
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating user status: " + e.getMessage());
        }
    }
}