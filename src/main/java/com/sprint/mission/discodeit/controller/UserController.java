package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@RequestMapping("/api/users")
@ResponseBody
@Controller
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @RequestMapping(
      path = "/create"
      //			,method = RequestMethod.POST  요청이 GET일지 POST일지 모른다.
      ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE   // 파일(프로필 이미지)이 있을수도 있고 없을수도 있다.
  )
  public ResponseEntity<User> create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                     @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileReq = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
    User createdUser = userService.createUser(userCreateRequest, profileReq);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    // 응답에 User의 password도 포함됨. 해시화 혹은 UserDto로 응답대체
  }

  @RequestMapping("/findAll")
  public ResponseEntity<List<UserDto>> findAll() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @RequestMapping(
      path = "update",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<User> update(@RequestParam("userId") UUID userId,
                                     @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                     @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
  }

  @RequestMapping("/delete")
  public ResponseEntity<Void> delete(@RequestParam("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping("/updateUserStatusByUserId")
  public ResponseEntity<UserStatus> updateUserStatusByUserId(@RequestParam("userId") UUID userId,
                                                             @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedUserStatus);
  }

  // MultiPartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if(profile.isEmpty()) {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
      return Optional.empty();
    } else {
      // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
      try {
        BinaryContentCreateRequest req = new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getBytes(),
            profile.getContentType());
        return Optional.of(req);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}


