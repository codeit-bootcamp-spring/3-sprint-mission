package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 사용자(User) 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 * <p>사용자 생성, 수정, 삭제, 조회, 상태 변경 기능을 제공합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private static final String CONTROLLER_NAME = "[UserController] ";

    /**
     * 신규 유저를 생성합니다.
     * @param userCreateRequest 유저 생성 요청 정보
     * @param profile 프로필 이미지 파일(선택)
     * @return 생성된 유저 DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<UserDto> create(
        @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info(CONTROLLER_NAME + "신규 유저 생성 요청: username={}, email={}",
                userCreateRequest.username(), userCreateRequest.email());

        if (profile != null) {
            log.info(CONTROLLER_NAME + "프로필 파일 업로드: filename={}, size={}",
                    profile.getOriginalFilename(), profile.getSize());
        } else {
            log.info(CONTROLLER_NAME + "프로필 파일 없음");
        }

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

        UserDto createdUser = userService.create(userCreateRequest, profileRequest);

        log.info(CONTROLLER_NAME + "신규 유저 생성 성공: userId={}", createdUser.id());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser);
    }

    /**
     * MultipartFile 타입의 프로필 파일을 BinaryContentCreateRequest로 변환합니다.
     * @param profile 프로필 파일
     * @return 변환된 BinaryContentCreateRequest(Optional)
     */
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                log.error(CONTROLLER_NAME + "프로필 처리 중 오류: filename={}", profile.getOriginalFilename(), e);
                throw new RuntimeException("프로필 처리 중 오류 발생", e);
            }
        }
    }

    /**
     * 유저 정보를 수정합니다.
     * @param userId 수정할 유저 ID
     * @param userUpdateRequest 유저 수정 요청 정보
     * @param profile 프로필 이미지 파일(선택)
     * @return 수정된 유저 DTO
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
        log.info(CONTROLLER_NAME + "유저 정보 수정 요청: userId={}", userId);
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

        log.info(CONTROLLER_NAME + "유저 정보 수정 성공: userId={}", userId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUser);
    }

    /**
     * 유저를 삭제합니다.
     * @param userId 삭제할 유저 ID
     * @return HTTP 204 No Content
     */
    @Parameter(name = "userId", description = "삭제할 User ID", required = true)
    @Override
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID userId
    ) {
        log.info(CONTROLLER_NAME + "유저 삭제 요청: userId={}", userId);
        userService.delete(userId);
        log.info(CONTROLLER_NAME + "유저 삭제 성공: userId={}", userId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    /**
     * 전체 유저 목록을 조회합니다.
     * @return 유저 DTO 목록
     */
    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        log.info(CONTROLLER_NAME + "전체 유저 목록 조회 요청");
        List<UserDto> userDtoDataList = userService.findAll();
        log.info(CONTROLLER_NAME + "전체 유저 목록 조회 성공: 건수={}", userDtoDataList.size());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDtoDataList);
    }

    /**
     * 유저 상태 정보를 수정합니다.
     * @param userId 상태를 수정할 유저 ID
     * @param userStatusUpdateRequest 상태 수정 요청 정보
     * @return 수정된 유저 상태 DTO
     */
    @PatchMapping(path = "/{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatus(
        @PathVariable UUID userId,
        @Valid @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        log.info(CONTROLLER_NAME + "유저 상태 정보 수정 요청: userId={}", userId);
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);
        log.info(CONTROLLER_NAME + "유저 상태 정보 수정 성공: userId={}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }
}
