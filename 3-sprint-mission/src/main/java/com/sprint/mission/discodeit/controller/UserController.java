package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserAPI;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * 1. 엔드포인트(End-point)
 *   - 엔드포인트는 URL과 HTTP 메서드로 구성됨.
 *   - 엔드포인트는 다른 API와 겹치지 않는(중복되지 않는) 유일한 값으로 정의할 것.
 * 2. 요청(Request)
 *   - 요청으로부터 어떤 값을 받아야 하는지 정의.
 *   - 각 값을 HTTP 요청의 header, body 등 어느 부분에서 어떻게 받을지 정의.
 * 3. 응답(Response) - 뷰 기반이 아닌 데이터 기반(객체 반환) 응답으로 작성.
 *   - 응답 상태 코드 정의
 *   - 응답 데이터 정의
 *   - (옵션) 응답 헤더 정의
 * */

/* 반환값에 따라 사용되는 메서드
 * 1. 논리 뷰 - ViewResolver, View
 * 2. Java 객체 - HttpResponse .....
 * */

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
@Slf4j
public class UserController implements UserAPI {

  private final UserService userService;
  private final UserStatusService userStatusService;

  // 신규 유저 생성 요청
  @Override
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserDto> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("사용자 생성 요청 request={}", userCreateRequest);

    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
    log.info("사용자 프로필 업로드 요청 profileRequest={}", profileRequest);

    UserDto createdUser = userService.create(userCreateRequest, profileRequest);
    log.info("사용자 생성 완료 createdUserId={}, profileId={}", createdUser.id(),
        createdUser.profile().id());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  // 유저 다건 조회
  @GetMapping
  @Override
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(users);
  }

  // 유저 정보 수정
  @PatchMapping(
      path = "{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("사용자 정보 수정 요청 userId={}, request={}", userId, userUpdateRequest);

    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
    log.info("사용자 프로필 업로드 요청 profileRequest={}", profileRequest);

    UserDto user = userService.update(userId, userUpdateRequest, profileRequest);
    log.info("사용자 정보 수정 완료 userId={}, profileId={}", userId, user.profile().id());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }

  // 유저 삭제
  @DeleteMapping(
      value = "/{userId}"
  )
  public ResponseEntity<String> delete(
      @PathVariable("userId") UUID userId
  ) {
    log.info("사용자 삭제 요청 userId={}", userId);

    UserDto user = userService.find(userId);
    String username = user.username();

    userService.delete(userId);
    log.info("사용자 삭제 완료 userId={}", userId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body("(" + username + ") 유저를 삭제했습니다.");
  }

  // 유저 상태 업데이트
  @PatchMapping(
      path = "{userId}/userStatus"
  )
  public ResponseEntity<UserStatusDto> updateUserStatusByUserId(
      @PathVariable("userId") UUID userId,
      @RequestBody UserStatusUpdateRequest request
  ) {
    log.info("사용자 상태 업데이트 요청 userId={}, request={}", userId, request);

    UserStatusDto userStatus = userStatusService.updateByUserId(userId, request);
    log.info("사용자 상태 업데이트 완료 userId={}, lastActiveAt={}", userId, userStatus.lastActiveAt());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userStatus);
  }

  // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {

    if (profileFile.isEmpty()) {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return Optional.empty();

    } else {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentCreateRequest binaryContentCreateRequest =
            new BinaryContentCreateRequest(
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
