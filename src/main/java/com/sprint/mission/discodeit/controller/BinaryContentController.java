package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/binarycontent")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ResponseEntity<BinaryContent> create(@RequestBody BinaryContentCreateRequest request) {
    return ResponseEntity.ok(binaryContentService.create(request));
  }

  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(@PathVariable UUID id) {
    return ResponseEntity.ok(binaryContentService.find(id));
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam List<UUID> ids) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
  }
}

