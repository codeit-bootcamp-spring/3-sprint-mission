package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;
  private final BinaryContentService binaryContentService;

  @RequestMapping(
      method = RequestMethod.POST,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<UserResponse> createUser(
      @RequestPart UserCreateRequest request,
      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
  ) {
    BinaryContentCreateRequest profileImageRequest = null;

    if (profileImage != null) {
      profileImageRequest = resolveProfileImageRequest(profileImage).orElse(null);
    }

    UserResponse response = userService.create(request, profileImageRequest);
    return ResponseEntity.created(URI.create("/api/user/" + response.id())).body(response);
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<UserResponse> updateUser(@PathVariable UUID userId,
      @RequestBody UserUpdateRequest request) {
    Optional<UserResponse> updated = userService.update(userId, request);
    return updated.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserResponse>> findAllUsers() {
    List<UserResponse> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
  public ResponseEntity<UserResponse> deleteUser(@PathVariable UUID userId) {
    Optional<UserResponse> deleted = userService.delete(userId);
    return deleted.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private Optional<BinaryContentCreateRequest> resolveProfileImageRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        );
        return Optional.of(request);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
