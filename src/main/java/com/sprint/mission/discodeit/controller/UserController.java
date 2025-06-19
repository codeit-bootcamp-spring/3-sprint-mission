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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 신규 유저 생성 요청
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<UserDto> create(
        @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        log.info("[UserController] 신규 유저 생성 요청 수신: username={}, email={}",
                userCreateRequest.username(), userCreateRequest.email());

        if (profile != null) {
            log.info("[UserController] 프로필 파일 업로드 됨: filename={}, size={}",
                    profile.getOriginalFilename(), profile.getSize());
        } else {
            log.info("[UserController] 프로필 파일 없음");
        }

        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

        UserDto createdUser = userService.create(userCreateRequest, profileRequest);

        log.info("[UserController] 신규 유저 생성 완료: userId={}",
                createdUser.id());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if (profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException("프로필 처리 중 오류 발생", e);
            }
        }
    }

    // 유저 정보 수정 요청
    @PatchMapping(
        path = "/{userId}"
        , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Override
    public ResponseEntity<UserDto> update(
        @PathVariable UUID userId,
        @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
                .flatMap(this::resolveProfileRequest);

        UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUser);
    }

    // 유저 삭제 요청
    @Parameter(name = "userId", description = "삭제할 User ID", required = true)
    @Override
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID userId
    ) {
        userService.delete(userId); // 이 안에 UserStatus, BinaryContent 삭제 로직 있음

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }


    // 유저 다건 조회 요청
    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> userDtoDataList = userService.findAll();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userDtoDataList);
    }

    // 유저 상태 정보 수정 요청
    @PatchMapping(path = "/{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatus(
        @PathVariable UUID userId,
        @Valid @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
    ) {
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId,
            userStatusUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
    }
}
