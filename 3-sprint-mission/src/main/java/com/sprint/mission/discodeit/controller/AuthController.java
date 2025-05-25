package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@ResponseBody
@Controller
public class AuthController {
    private final AuthService authService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            value = "/login"
//            , method = RequestMethod.PUT
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> login(
            @RequestBody LoginRequest loginDTO
    ) {
        User user = authService.login(loginDTO);
        String username = user.getUsername();

        UserStatusUpdateRequest userStatusUpdateDTO =
                new UserStatusUpdateRequest(Instant.now());

        userStatusService.updateByUserId(user.getId(), userStatusUpdateDTO);

        System.out.println("로그인 유저 : " + user);
        return ResponseEntity.ok(
                username + "(으)로 로그인했습니다."
        );
    }
}
