package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @Valid
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

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
        throw new RuntimeException(e);
      }
    }
  }


  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(
      @PathVariable UUID userId
      , @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest
      , @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    UserDto updatedUser = userService.update(
        userId,
        userUpdateRequest,
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest)
    );
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }


  @DeleteMapping("/{userId}")
  public ResponseEntity<String> delete(@PathVariable UUID userId) {
    try {
      userService.delete(userId);
      return ResponseEntity.ok("사용자 삭제에 성공했습니다");
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("해당 사용자를 찾을 수 없습니다");
    }
  }


  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(users);
  }


  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest userStatusUpdateRequest
  ) {
    System.out.println("userId : " + userId);
    System.out.println("userStatusUpdateRequest : " + userStatusUpdateRequest);
    UserStatusDto updatedStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(updatedStatus);
  }

}

