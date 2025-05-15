package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @GetMapping("/binary-contents")
  public ResponseEntity<List<BinaryContent>> findAll(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> allByIdIn = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok().body(allByIdIn);
  }

  @GetMapping(value = "/binary-contents/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent allByIdIn = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok().body(allByIdIn);
  }

}
