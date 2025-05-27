package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.utils.BinaryContentConverter;
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


/* API 구현 절차
    1. 엔드포인트(End-point)
     - 엔드포인트는 URL과 HTTP 메서드로 구성됨
     - 엔드포인트는 다른 API와 겹치지 않는 유일한 값으로 정의할것.
     2. 요청
     - 요청으로부터 어떤 값을 받아야하는지 정의
     - 각 값을 HTTP 요청의 Header, Body 등 어떤 부분에서 어떻게 받을지 정의.
     3. 응답 ( 뷰 기반이 아닌 데이터 기반 응답으로 작성
     - 응답 상태 코드 정의
     - 응답 데이터 정의
     -(옵션) 응답 헤더 정의
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserStatusService userStatusService;

  /* 유저 생성 */
  @PostMapping(consumes = {
      MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(BinaryContentConverter::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  /* 유저 수정 */
  @PatchMapping(path = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<User> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest updateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(BinaryContentConverter::resolveProfileRequest);
    User updatedUser = userService.update(userId, updateRequest,
        profileRequest);

    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  /* 유저 삭제 */
  @DeleteMapping(path = "/{userId}")
  @Override
  public ResponseEntity<Void> delete(
      @PathVariable("userId") UUID userId
  ) {
    userService.delete(userId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /* 유저 조회 All */
  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  /* 유저 상태 업데이트 */
  @PatchMapping(path = "/{userId}/userStatus")
  @Override
  public ResponseEntity<UserStatus> updateUserStatusByUserId(
      @PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest userUpdateRequest
  ) {
    UserStatus userStatus = userStatusService.updateByUserId(userId,
        userUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(userStatus);
  }

}
