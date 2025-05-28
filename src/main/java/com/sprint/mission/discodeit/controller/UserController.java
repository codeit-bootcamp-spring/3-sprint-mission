package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)   // 파일(프로필 이미지)이 있을수도 있고 없을수도 있다.
    @Override
    public ResponseEntity<UserResponse> create(
        @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileReq = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
        User createdUser = userService.createUser(userCreateRequest, profileReq);

        UserResponse response = UserResponse.fromEntity(createdUser);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(users);
    }

    @PatchMapping(
        path = "/{userId}",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @Override
    public ResponseEntity<UserResponse> update(@PathVariable("userId") UUID userId,
        @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
        User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);

        UserResponse response = UserResponse.fromEntity(updatedUser);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @DeleteMapping("/{userId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @PatchMapping("/{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusResponse> updateUserStatusByUserId(
        @PathVariable("userId") UUID userId,
        @Valid @RequestBody UserStatusUpdateRequest request) {
        UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
        UserStatusResponse response = UserStatusResponse.fromEntity(updatedUserStatus);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest req = new BinaryContentCreateRequest(
                    profile.getOriginalFilename(),
                    profile.getBytes(),
                    profile.getContentType());
                return Optional.of(req);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}