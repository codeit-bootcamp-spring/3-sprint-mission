package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.global.exception.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

  private final BinaryContentRepository binaryContentRepository;

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> find(@RequestParam UUID binaryContentId) {
    BinaryContent content = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId.toString()));

    Map<String, Object> response = new HashMap<>();
    response.put("filename", content.getFilename());
    response.put("contentType", content.getContentType());
    response.put("bytes", Base64.getEncoder().encodeToString(content.getData())); // 🔥 핵심!

    return ResponseEntity.ok(response);
  }

}
