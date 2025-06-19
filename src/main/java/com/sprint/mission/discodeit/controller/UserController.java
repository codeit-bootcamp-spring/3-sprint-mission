package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User 컨트롤러", description = "스프린트 미션5 유저 컨트롤러 엔트포인트들 입니다.")
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;


    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(summary = "사용자 생성", description = "사용자를 생성합니다. 이미지는 옵션입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @RequestPart("userCreateRequest") UserCreateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profileFile) {
        Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profileFile)
                .flatMap(this::resolveProfileRequest);
        return ResponseEntity.status(201).body(userService.create(request, profileRequest));
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다. 프로필 사진, 프로필 사진 정보, 유저 상태가 같이 삭제됩니다.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 이름, 비밀번호, 이메일, 이미지를 수정합니다.")
    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<?> update(
            @PathVariable UUID userId,
            @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profile", required = false) MultipartFile profileFile) {
        return ResponseEntity.ok(userService.update(userId, request, profileFile));
    }

    //0 USER STATUS 에서 가져온 메서드
    // 관심사 분리를 위해선 userStatus에서 하는게 맞지 않나? 메서드가 하나라 그냥 하는건가?
    @Operation(summary = "사용자 활동상태 수정", description = "사용자의 최근 접속시간을 수정합니다.")
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
