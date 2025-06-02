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

  // 신규 유저 생성 요청( POST )
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> create(
      @Valid
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    // 선택적 유저 프로필 이미지 처리
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
  // 사용자 정보 수정( PATCH )
  public ResponseEntity<UserDto> update(
      // 어느 사용자인지 식별
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


  // 사용자 삭제( DEL )
  @DeleteMapping("/{userId}")
  public ResponseEntity<String> delete(@PathVariable UUID userId) {
    try {
      userService.delete(userId);
      // HTTP 상태 코드 200( ok ) 반환
      return ResponseEntity.ok("사용자 삭제에 성공했습니다");
      // 사용자를 찾을 수 없다면 예외 발생 처리
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("해당 사용자를 찾을 수 없습니다");
    }
  }


  // 모든 사용자 조회( GET )
  @GetMapping
  public ResponseEntity<List<UserDto>> findAll() {
    // 모든 사용자 조회
    List<UserDto> users = userService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(users);
  }


  // 사용자 활동 상태 변경
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

