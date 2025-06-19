package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateByUserIdRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.controller.api
 * FileName     : UserApi
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */


@Tag(name = "User 컨트롤러", description = "스프린트 미션5 유저 컨트롤러 엔트포인트들 입니다.")
@RequestMapping("api/users")
public interface UserApi {

    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 정보를 조회합니다.")
    @GetMapping
    ResponseEntity<?> findAll();

    @Operation(summary = "사용자 생성", description = "사용자를 생성합니다. 이미지는 옵션입니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> create(
        @RequestPart("userCreateRequest") UserCreateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profileFile);

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다. 프로필 사진, 프로필 사진 정보, 유저 상태가 같이 삭제됩니다.")
    @DeleteMapping("/{userId}")
    ResponseEntity<?> delete(@PathVariable UUID userId);

    @Operation(summary = "사용자 정보 수정", description = "사용자 이름, 비밀번호, 이메일, 이미지를 수정합니다.")
    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<JpaUserResponse> update(
        @PathVariable UUID userId,
        @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profileFile);

    @Operation(summary = "사용자 활동상태 수정", description = "사용자의 최근 접속시간을 수정합니다.")
    @PatchMapping("/{userId}/userStatus")
    ResponseEntity<?> updateTime(
        @PathVariable UUID userId,
        @Valid @RequestBody UserStatusUpdateByUserIdRequest request);
}
