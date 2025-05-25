package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 *  GET  /api/binaryContents/{binaryContentId}
 *  GET  /api/binaryContents?binaryContentIds={id}&binaryContentIds={id}
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.find(binaryContentId));
  }

  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
          @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }
}
