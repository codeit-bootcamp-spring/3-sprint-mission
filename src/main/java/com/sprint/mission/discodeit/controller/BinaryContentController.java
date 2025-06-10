package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.global.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> findById(@PathVariable("id") UUID id) {
    System.out.println("📥 [GET] /api/binaryContents/" + id + " called");

    BinaryContentDto content = binaryContentService.find(id)
        .orElse(null);

    if (content == null) {
      System.out.println("❌ Binary content not found for UUID: " + id);
      return ResponseEntity.notFound().build();
    }

    System.out.println("✅ Found binary content: filename=" + content.fileName() + ", contentType="
        + content.contentType());

    return ResponseEntity.ok(Map.of(
        "filename", content.fileName(),
        "contentType", content.contentType()
    ));
  }


  @GetMapping("/findAllByIdIn")
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentDto> contents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(contents);
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<?> download(@PathVariable("id") UUID id) {
    BinaryContentDto content = binaryContentService.find(id)
        .orElseThrow(() -> new BinaryContentNotFoundException(id.toString()));

    return binaryContentStorage.download(content);
  }
}
