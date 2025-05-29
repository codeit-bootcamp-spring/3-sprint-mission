package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.UserRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.global.response.CustomApiResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
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

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<CustomApiResponse<UserResponse>> create(
      @RequestPart("userCreateRequest") UserRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CustomApiResponse.created(userService.create(userCreateRequest, profile)));
  }

  @PatchMapping(
      path = "{userId}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @Override
  public ResponseEntity<CustomApiResponse<UserResponse>> update(
      @PathVariable("userId") UUID userId,
      @RequestPart("userUpdateRequest") UserRequest.Update userRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(CustomApiResponse.success(userService.update(userId, userRequest, profile)));
  }

  @DeleteMapping(path = "{userId}")
  @Override
  public ResponseEntity<CustomApiResponse<Void>> delete(@PathVariable("userId") UUID userId) {
    userService.delete(userId);
    return ResponseEntity.ok(CustomApiResponse.success("유저 삭제 성공"));
  }

  @GetMapping
  @Override
  public ResponseEntity<CustomApiResponse<List<UserResponse>>> findAll() {
    return ResponseEntity.ok(CustomApiResponse.success(userService.findAll()));
  }

  @PatchMapping(path = "{userId}/userStatus")
  @Override
  public ResponseEntity<CustomApiResponse<UserStatusResponse>> updateUserStatusByUserId(
      @PathVariable("userId") UUID userId,
      @RequestBody UserStatusRequest.Update request) {
    return ResponseEntity.ok(
        CustomApiResponse.success(userStatusService.updateByUserId(userId, request))
    );
  }
}
