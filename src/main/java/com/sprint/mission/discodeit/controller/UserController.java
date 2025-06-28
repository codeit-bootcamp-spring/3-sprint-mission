package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.util.BinaryContentUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/* API 구현 절차
 * 1. 엔드포인트(End-point)
 *  - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *  - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것
 * 2. 요청(Request)
 *  - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *  - 각 값을 HTTP 요청의 Header, Body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반 응답으로 작성
 *  - 응답 상태 코드 정의
 *  - 응답 데이터 정의
 *  - (옵션) 응답 헤더 정의
 * */

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;

    /**
     * 신규 사용자 등록
     *
     * @param userCreateRequest 유저 생성 요청 DTO
     * @param profile           프로필 이미지
     * @return 생성된 User (HTTP 201 CREATED)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        BinaryContentCreateRequest profileRequestDTO = null;

        if (profile != null) {
            profileRequestDTO = BinaryContentUtil.resolveFile(profile).orElse(null);
        }

        UserDto createdUser = userService.create(userCreateRequest, profileRequestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    /**
     * 사용자 정보 수정
     *
     * @param userId            수정할 사용자 ID
     * @param userUpdateRequest 유저 수정 요청 DTO
     * @param profile           수정할 프로필 이미지
     * @return 수정된 User (HTTP 200 OK)
     */
    @PatchMapping(
            path = "/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Override
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        BinaryContentCreateRequest profileRequestDTO = null;

        if (profile != null) {
            profileRequestDTO = BinaryContentUtil.resolveFile(profile).orElse(null);
        }

        UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    /**
     * 사용자 삭제
     *
     * @param userId 삭제할 유저 ID
     * @return 삭제 완료 메시지 (HTTP 200 OK)
     */
    @DeleteMapping(path = "/{userId}")
    @Override
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId
    ) {
        userService.delete(userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * 모든 사용자 조회
     *
     * @return 조회된 전체 User 목록 (HTTP 200 OK)
     */
    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAll() {

        List<UserDto> userDtoList = userService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtoList);
    }

    /**
     * 사용자의 온라인 상태 업데이트
     *
     * @param userId                  대상 사용자 ID
     * @param userStatusUpdateRequest 유저상태 수정 요청 DTO
     * @return 업데이트 된 UserStatus (HTTP 200 OK)
     */
    @PatchMapping(path = "/{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId,
                userStatusUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUserStatus);
    }
}
