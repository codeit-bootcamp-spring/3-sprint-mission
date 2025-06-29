package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.dto.mapper.ResponseMapper;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @Override
  public ResponseEntity<UserResponse> create(
      @Valid @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.info("사용자 생성 요청 - 사용자명: {}", userCreateRequest.username());
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    UserResponse response = ResponseMapper.toResponse(createdUser);
    log.info("사용자 생성 완료 - 사용자 ID: {}", createdUser.id());
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  @PatchMapping(path = "{userId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @Override
  public ResponseEntity<UserResponse> update(
      @PathVariable("userId") UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    log.info("사용자 수정 요청 - 사용자 ID: {}", userId);
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    UserDto updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    UserResponse response = ResponseMapper.toResponse(updatedUser);
    log.info("사용자 수정 완료 - 사용자 ID: {}", userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @DeleteMapping(path = "{userId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
    log.info("사용자 삭제 요청 - 사용자 ID: {}", userId);
    userService.delete(userId);
    log.info("사용자 삭제 완료 - 사용자 ID: {}", userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    log.info("전체 사용자 조회 요청");
    List<UserDto> users = userService.findAll();
    log.info("전체 사용자 조회 완료 - 조회된 사용자 수: {}", users.size());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  @PatchMapping(path = "{userId}/userStatus")
  @Override
  public ResponseEntity<UserStatusResponse> updateUserStatusByUserId(@PathVariable("userId") UUID userId,
      @Valid @RequestBody UserStatusUpdateRequest request) {
    log.info("사용자 상태 수정 요청 - 사용자 ID: {}, 마지막 활동 시간: {}", userId, request.newLastActiveAt());
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    UserStatusResponse response = ResponseMapper.toResponse(updatedUserStatus);
    log.info("사용자 상태 수정 완료 - 사용자 ID: {}", userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      log.debug("프로필 파일이 비어있음");
      return Optional.empty();
    } else {
      try {
        log.debug("프로필 파일 처리 시작 - 파일명: {}, 크기: {} bytes",
            profileFile.getOriginalFilename(), profileFile.getSize());
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes());
        log.debug("프로필 파일 처리 완료");
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        log.error("프로필 파일 읽기 실패 - 파일명: {}, 오류: {}",
            profileFile.getOriginalFilename(), e.getMessage());
        throw BinaryContentNotFoundException.of();
      }
    }
  }
}
