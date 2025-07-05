package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
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
@RequestMapping("/api/readstatus")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    return ResponseEntity.ok(readStatusService.create(request));
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatus>> findAllByUser(@PathVariable UUID userId) {
    return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
  }

  @RequestMapping(value = "/{readStatusId}", method = RequestMethod.PUT)
  public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId) {
    return ResponseEntity.ok(readStatusService.update(readStatusId));
  }
}

