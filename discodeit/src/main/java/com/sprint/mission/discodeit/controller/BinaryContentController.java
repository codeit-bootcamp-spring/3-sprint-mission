package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/binary-contents")
@RestController
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;


  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> findBinaryContentById(
      @PathVariable("binaryContentId") UUID contentId) {
    return ResponseEntity.ok(binaryContentService.findById(contentId));
  }

  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllBinaryContentByIds(
      @RequestBody List<UUID> contentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(contentIds));
  }

}
