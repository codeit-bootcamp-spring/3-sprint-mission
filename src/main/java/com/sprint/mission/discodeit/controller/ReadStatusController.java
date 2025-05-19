package com.sprint.mission.discodeit.controller;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/read-status")
class ReadStatusController {

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<?> createReadStatus() {
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateReadStatus(@PathVariable UUID id) {
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public ResponseEntity<?> findReadStatusByUser(@PathVariable UUID userId) {
    return ResponseEntity.ok().build();
  }
}