package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.global.response.CustomApiResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
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
public class BinaryContentController implements BinaryContentApi {


  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(path = "{binaryContentId}")
  @Override
  public ResponseEntity<BinaryContentResponse> getFile(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentResponse binaryContentResponse = binaryContentService.findByIdOrThrow(
        binaryContentId);
    return ResponseEntity.ok(binaryContentResponse);
  }

  @GetMapping
  @Override
  public ResponseEntity<List<BinaryContentResponse>> getFileList(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentResponse> binaryContentResponses = binaryContentService.findAllByIdIn(
        binaryContentIds);
    return ResponseEntity.ok(binaryContentResponses);
  }

  @GetMapping("/{binaryContentId}/download")
  @Override
  public ResponseEntity<?> downloadFile(@PathVariable UUID binaryContentId) {
    return binaryContentStorage.download(binaryContentService.findByIdOrThrow(binaryContentId));
  }
}
