package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> create(
          @RequestPart("userCreateRequest") UserCreateRequest request,
          @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<BinaryContentCreateRequest> pic = Optional.ofNullable(profile).map(this::toBinaryRequest);
    User created = userService.create(request, pic);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);   // 201
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<User> update(
          @PathVariable UUID userId,
          @RequestPart("userUpdateRequest") UserUpdateRequest request,
          @RequestPart(value = "profile", required = false) MultipartFile profile) {

    Optional<BinaryContentCreateRequest> pic = Optional.ofNullable(profile)
            .map(this::toBinaryRequest);
    return ResponseEntity.ok(userService.update(userId, request, pic));
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();                        // 204
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
          @PathVariable UUID userId,
          @RequestBody UserStatusUpdateRequest request) {

    return ResponseEntity.ok(
            userStatusService.updateByUserId(userId, request)
    );
  }

  /* ---------- util ---------- */
  private BinaryContentCreateRequest toBinaryRequest(MultipartFile f) {
    try {
      return new BinaryContentCreateRequest(
              f.getOriginalFilename(),
              f.getContentType(),
              f.getSize(),
              f.getBytes());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
