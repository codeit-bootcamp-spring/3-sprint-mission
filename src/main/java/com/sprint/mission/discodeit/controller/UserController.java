package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.global.exception.ProfileImageProcessingException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> createUser(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserResponse createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> findAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  @PatchMapping(
      path = "/{userId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserResponse updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity.ok(updatedUser);
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.ok(updatedUserStatus);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile == null || profile.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(new BinaryContentCreateRequest(
          profile.getOriginalFilename(),
          profile.getContentType(),
          profile.getBytes()
      ));
    } catch (IOException e) {
      throw new ProfileImageProcessingException(profile.getOriginalFilename());
    }
  }
}
