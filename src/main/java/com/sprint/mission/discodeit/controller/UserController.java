package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.assembler.UserCommandAssembler;
import com.sprint.mission.discodeit.controller.api.UserApi;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.command.CreateUserCommand;
import com.sprint.mission.discodeit.service.command.UpdateUserCommand;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

  private final UserService userService;
  private final UserCommandAssembler userCommandAssembler;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> create(
      @RequestPart @Valid UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    CreateUserCommand command = userCommandAssembler.toCreateCommand(userCreateRequest, profile);

    UserResponse response = userService.create(command);
    return ResponseEntity.created(URI.create("/api/users/" + response.id())).body(response);
  }

  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> update(
      @PathVariable UUID userId,
      @RequestPart @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    UpdateUserCommand command = userCommandAssembler.toUpdateCommand(userId, userUpdateRequest,
        profile);

    UserResponse updated = userService.update(command);
    return ResponseEntity.ok(updated);
  }

  @GetMapping("/email")
  public ResponseEntity<UserResponse> findByEmail(@RequestParam String email) {
    UserResponse response = userService.findByEmail(email);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> findAll() {
    List<UserResponse> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> delete(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }

}
