package com.sprint.mission.discodeit.Controller;

import com.sprint.mission.discodeit.Controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // 유저 생성 (POST /api/users)
  @Override
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    User createdUser;
    if (profile != null && profile.getSize() > 0) {
      BinaryContentCreateRequest portraitRequest = resolveProfileRequest(profile);
      createdUser = userService.create(userCreateRequest, portraitRequest);
    } else {
      createdUser = userService.create(userCreateRequest);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.fromEntity(createdUser));
  }

  // 유저 수정 (PUT /api/users/{userId})
  @Override
  @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId,
      @RequestPart(value = "userUpdateRequest", required = false) UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    User updatedUser = null;
    if (userUpdateRequest != null) {
      updatedUser = userService.update(userId, userUpdateRequest);
    }
    if (profile != null && profile.getSize() > 0) {
      BinaryContentCreateRequest portraitRequest = resolveProfileRequest(profile);
      updatedUser = userService.update(userId, portraitRequest);
    }
    return ResponseEntity.ok(UserDto.fromEntity(updatedUser));
  }

  // 유저 삭제 (DELETE /api/users/{userId}
  @Override
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  // 전체 유저 조회 (Get /api/users)
  @Override
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  //유저 상태 업데이트 (PATCH /api/users/{uerId}/status)
  @Override
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(updatedUserStatus);
  }


    private BinaryContentCreateRequest resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return null;
        } else {
            try {
                return new BinaryContentCreateRequest(
                        profileFile.getOriginalFilename(),
                        profileFile.getContentType(),
                        profileFile.getBytes()
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
