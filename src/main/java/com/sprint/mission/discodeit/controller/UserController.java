package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
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


@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;

    // 유저 전체 조회
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }


    // 신규 유저 생성 요청
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(
            @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        UserDto createdUser = userService.create(userCreateRequest, profileRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }


    //유저 정보 수정
    @PatchMapping(path = "/{userId}"
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserDto> update(
            @PathVariable("userId") UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> request =
                Optional.ofNullable(profile)
                        .flatMap(this::resolveProfileRequest);

        UserDto updatedUser = userService.update(userId, userUpdateRequest, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }


    //유저 삭제
    @DeleteMapping(path = "{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable("userId") UUID userId
    ){
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }


    // 유저 상태 업데이트 요청
    @PatchMapping(path = "{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        UserStatusDto updatedStatus = userStatusService.updateByUserId(userId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedStatus);
    }


    // 단일 파일 처리
    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {

        if (profile == null || profile.isEmpty()) {
            // 기본 이미지 경로로부터 BinaryContentCreateRequest 생성
            try {
                ClassPathResource resource = new ClassPathResource("static/images/default-avatar.png");
                byte[] bytes = resource.getInputStream().readAllBytes();

                BinaryContentCreateRequest defaultRequest = new BinaryContentCreateRequest(
                        "default-avatar.png",
                        "image/png",
                        bytes
                );
                return Optional.of(defaultRequest);
            } catch (IOException e) {
                throw new RuntimeException("기본 이미지 파일을 읽는 중 오류가 발생했습니다.", e);
            }
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
                throw new RuntimeException(e);
            }
        }
    }

}
