package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.unit.UserService;
import com.sprint.mission.discodeit.unit.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller fileName       : UserController author :
 * doungukkim date           : 2025. 5. 8. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 8.        doungukkim 최초 생성
 */
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final UserStatusService userStatusService;


    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profileFile) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profileFile)
                .flatMap(this::resolveProfileRequest);
        return ResponseEntity.status(201).body(userService.create(request, profileRequest));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }

    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JpaUserResponse> update(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profileFile) {
        return ResponseEntity.ok(userService.update(userId, request, profileFile));
    }

    //0 USER STATUS 에서 가져온 메서드
    // 관심사 분리를 위해선 userStatus에서 하는게 맞지 않나? 메서드가 하나라 그냥 하는건가?
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<?> updateTime(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateByUserIdRequest request) {
        return ResponseEntity.status(200).body(userStatusService.updateByUserId(userId, request.newLastActiveAt()));
    }

    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
