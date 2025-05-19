package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.global.exception.ProfileImageProcessingException;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/user")
public class UserController {

  private final UserService userService;


  @RequestMapping(
      path = "/create"
      , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<UserResponse> create(
      @RequestPart("user") UserCreateRequest usercreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    Optional<BinaryContentCreateRequest> profileRequest =
        Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);
    UserResponse createdUser = userService.create(usercreateRequest, profileRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);

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

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public ResponseEntity<UserDto> find(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.find(userId));
  }

  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public ResponseEntity<List<UserDto>> findAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID userId,
      @RequestBody UserUpdateRequest request) {
    UserResponse response = userService.update(userId, request, Optional.empty());
    return ResponseEntity.ok(response);
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<UserResponse> delete(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.delete(userId));
  }
}

