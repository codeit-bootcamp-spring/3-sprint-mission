package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.NoSuchElementException;


@RequiredArgsConstructor
@RequestMapping("/api/login")
@Controller
public class AuthController {
    private final AuthService authService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            User user = authService.login(loginRequest);

            boolean isOnline = false;
            try {
                // 활동 상태( 유동적 )
                UserStatus userStatus = userStatusService.find(user.getUserId());
                isOnline = userStatus.isOnline();
            } catch (NoSuchElementException e) {
                // 상태정보가 없으면 기본값( false ) 유지
            }

            // 로그인 성공 시
            UserDTO userDTO = new UserDTO(
                    user.getUserId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUserName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getStatusMessage(),
                    user.getProfileId(),
                    isOnline
            );
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
