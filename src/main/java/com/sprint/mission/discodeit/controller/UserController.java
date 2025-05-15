package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
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
@Controller
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/api")
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;


  /* 유저 생성 */
  @RequestMapping(path = "users", method = RequestMethod.POST, consumes = {
      MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> create(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity.created(URI.create(createdUser.getId().toString())).body(createdUser);
//    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  /* 유저 수정 */
  @RequestMapping(path = "users/update/{userId}", method = RequestMethod.PUT, consumes = {
      MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<User> update(
      @PathVariable String userId,
//            @RequestParam String userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest updateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {

    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(parseStringToUuid(userId), updateRequest, profileRequest);
    return ResponseEntity.ok().body(updatedUser);
  }

  /* 유저 삭제 */
  @RequestMapping(path = "users/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(
      @PathVariable String userId
  ) {
    userService.delete(parseStringToUuid(userId));
    //TODO : 204번으로 리턴
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /* 유저 조회 All */
  @RequestMapping(path = "user/findAll", method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    System.out.println(users.toString());
    return ResponseEntity.ok().body(users);
  }

  /* 유저 상태 업데이트 */
  @RequestMapping(path = "user-status/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<UserStatus> updateStatus(
      @PathVariable String userId,
      @RequestBody UserStatusUpdateRequest userUpdateRequest
  ) {
    UserStatus userStatus = userStatusService.updateByUserId(parseStringToUuid(userId),
        userUpdateRequest);
    return ResponseEntity.ok().body(userStatus);
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* MultipartFile 타입 -> BinaryContentCreateRequest 타입으로 변경 */
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }

  //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
  /* String 타입 -> UUID 타입으로 변경 */
  private UUID parseStringToUuid(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
    }
  }

}
