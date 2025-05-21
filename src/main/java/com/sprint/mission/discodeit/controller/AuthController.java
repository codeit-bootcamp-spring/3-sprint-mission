package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @RequestMapping(
            path = "/login"
            , method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserDto> login(
            @RequestBody LoginRequest loginRequest) {


        User user = authService.login(loginRequest);
        UserDto userDto = userService.find(user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(userDto);

    }
}
