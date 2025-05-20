package com.sprint.mission.discodeit.controller.user;

import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryOwnerType;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.userStatus.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/* API 구현 절차
 * 1. 엔드포인트 (End-point)
 *   - 엔드포인트는 URL과 HTTP 메서드로 구성됨
 *   - 엔드포인트는 다른 API와 겹치지 않는 유일한 값으로 정의할 것
 * 2. 요청(Request)
 *   - 요청으로부터 어떤 값을 받아야 하는지 정의
 *   - 각 값을 HTTP 요청의 Header, body 등 어느 부분에서 어떻게 받을지 정의
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성
 *   - 응답 상태 코드 정의
 *   - 응답 데이터 정의
 *   - (옵션) 응답 헤더 정의
 * */
@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST
    )
    @ResponseBody
    public ResponseEntity<UserResponseDto> create(@RequestPart("userCreateDto") UserCreateDto userCreateDto,
                                                  @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {

        UserResponseDto user = null;
        try {
            List<byte[]> profileFiles = new ArrayList<>();
            profileFiles.add(profile.getBytes());
            BinaryContentCreateRequestDto binaryContent = new BinaryContentCreateRequestDto(
                    BinaryContentType.PROFILE_IMAGE,
                    BinaryOwnerType.USER,
                    profile.getContentType(),
                    profileFiles
            );
            user = userService.createUser(userCreateDto, binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(path = "/modify", method = RequestMethod.PUT)
    public ResponseEntity<?> modify(
            @RequestPart("userUpdateRequestDto") UserUpdateRequestDto userUpdateRequestDto,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        try {
            List<byte[]> profileFiles = new ArrayList<>();
            profileFiles.add(profile.getBytes());
            BinaryContentCreateRequestDto binaryContent = new BinaryContentCreateRequestDto(
                    BinaryContentType.PROFILE_IMAGE,
                    BinaryOwnerType.USER,
                    profile.getContentType(),
                    profileFiles
            );
            boolean result = userService.modifyUser(userUpdateRequestDto, binaryContent);

            if (!result) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
            }

            return ResponseEntity.ok().body("Success");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        boolean result = userService.deleteUser(userId);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PUT)
    public ResponseEntity<?> modifyStatus(@PathVariable UUID userId) {
        boolean result = userStatusService.updateByUserId(userId);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }
}
