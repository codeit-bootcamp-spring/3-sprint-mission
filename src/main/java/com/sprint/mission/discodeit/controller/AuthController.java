package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 *
 * <p>로그인 기능을 제공하며, 클라이언트로부터 로그인 요청을 받아
 * {@link AuthService}를 통해 인증 로직을 수행합니다.</p>
 *
 * <p>요청에 대한 로깅을 수행하며, 클라이언트 IP, User-Agent, 처리 시간을 기록합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final HttpServletRequest request;
//
    private static final String CONTROLLER_NAME = "[AuthController] ";


    /**
     * 사용자의 로그인 요청을 처리합니다.
     *
     * <p>클라이언트가 로그인 정보를 담은 {@link LoginRequest} JSON을 전송하면,
     * 로그인에 성공한 사용자 정보를 {@link UserDto} 형태로 반환합니다.</p>
     *
     * @param loginRequest 로그인 요청 정보 (아이디, 비밀번호 등)
     * @return {@link ResponseEntity} 로그인 성공 시 사용자 정보와 200 OK 상태를 반환
     */
    @PostMapping(
        value = "/login"
//        , consumes = "application/json"        // 클라이언트가 JSON 보내고
//        , produces = "application/json"        // 서버도 JSON 응답함
    )
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        long startTime = System.currentTimeMillis();

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        log.info(CONTROLLER_NAME + "{} IP의 {}로부터 로그인 시도", ip, userAgent);
        log.info(CONTROLLER_NAME + "로그인 유저 이름: {}", loginRequest.username());

        UserDto loginUser = authService.login(loginRequest);
        long endTime = System.currentTimeMillis();
        log.info(CONTROLLER_NAME + "로그인 유저 정보: {}", loginUser);
        log.info(CONTROLLER_NAME + "로그인 진행 시간: {} ms", endTime - startTime);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(loginUser);
    }
}
