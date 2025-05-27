package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.global.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.Base64;
import java.util.HashMap;
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
  
  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> findById(@PathVariable("id") UUID id) {
    BinaryContent content = binaryContentService.find(id)
        .orElseThrow(() -> new BinaryContentNotFoundException(id.toString()));

    Map<String, Object> response = new HashMap<>();
    response.put("filename", content.getFilename());
    response.put("contentType", content.getContentType());
    response.put("bytes", Base64.getEncoder().encodeToString(content.getData()));

    return ResponseEntity.ok(response);
  }

  @GetMapping("/findAllByIdIn")
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(contents);
  }
}
