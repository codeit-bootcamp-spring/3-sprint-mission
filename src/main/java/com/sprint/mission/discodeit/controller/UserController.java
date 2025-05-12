package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
    UserResponse response = userService.create(request);
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
}
