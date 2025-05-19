package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/userstatus")
public class UserStatusController {

  private final UserStatusService userStatusService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<UserStatus> find(@PathVariable UUID id) {
    return ResponseEntity.ok(userStatusService.find(id));
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<UserStatus>> findAll() {
    return ResponseEntity.ok(userStatusService.findAll());
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<UserStatus> create(@RequestBody UserStatusCreateRequest request) {
    return ResponseEntity.ok(userStatusService.create(request));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<UserStatus> update(
      @PathVariable UUID id,
      @RequestBody UserStatusUpdateRequest request) {
    return ResponseEntity.ok(userStatusService.update(id, request));
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<UserStatus> updateByUserId(
      @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    return ResponseEntity.ok(userStatusService.updateByUserId(userId, request));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<UserStatus> delete(@PathVariable UUID id) {
    return ResponseEntity.ok(userStatusService.delete(id));
  }
}
