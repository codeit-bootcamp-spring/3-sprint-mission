package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* API 구현 절차
* 1. 엔드포인트(End-point)
*   - 엔드포인트는 URL과 HTTP 메서드로 구성됨
*   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것
* 2. 요청 (Request)
*   - 요청으로부터 어떤 값을 받아야 하는지 정의
*   - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의
* 3. 응답 (Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성
*   - 응답 상태 코드 정의
*   - 응답 데이터 정의
*   - (옵션) 응답 헤더 정의
* */

@RequiredArgsConstructor
@RequestMapping("/api/user")
@Controller
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    
    // 신규 유저 생성 요청
    @RequestMapping(
            path = "/create"
            , method = RequestMethod.POST
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<User> create(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false)MultipartFile profile //BinaryContentCreateRequest profileRequestNullable
    ) {

        BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
        User createdUser = userService.create(userCreateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 사용자 정보 수정
    @RequestMapping(
            path = "/update"
            , method = RequestMethod.PUT
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<User> update(
            @RequestParam("id") UUID userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        BinaryContentCreateRequest profileRequest = resolveProfileRequest(profile);
        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    // 사용자 삭제
    @RequestMapping(path = "/delete"
            , method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@RequestParam("id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 모든 사용자 조회
    @RequestMapping(path = "/find-all"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    // 사용자 온라인 상태 업데이트
    @RequestMapping(path = "/update-status"
            , method = RequestMethod.PATCH
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> updateStatus(
            @RequestParam("id") UUID userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변경하기 위한 메서드
    private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profile) {

        if (profile == null || profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return null;
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getBytes(),
                        profile.getContentType()
                );
                return binaryContentCreateRequest;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
