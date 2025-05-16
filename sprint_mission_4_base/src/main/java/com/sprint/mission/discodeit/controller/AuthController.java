package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Controller
public class AuthController {

    private final AuthService authService;
    private final UserStatusService userStatusService;


    @RequestMapping(
            value = "/login"
            , method = RequestMethod.POST
            , consumes = "application/json"        // 클라이언트가 JSON 보내고
            , produces = "application/json"        // 서버도 JSON 응답함
    )
    @ResponseBody
    public ResponseEntity<User> login(
            @RequestBody LoginRequest loginRequest) {

        User loginUser = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginUser);
    }
}
