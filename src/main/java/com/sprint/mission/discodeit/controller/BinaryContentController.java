package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<byte[]> download(
      @PathVariable("binaryContentId") UUID binaryContentId) {

    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

    // 기존 방식: return binaryContentStorage.download(binaryContentDto);
    // 새로운 방식: 바이너리 데이터를 직접 반환
    try (InputStream inputStream = binaryContentStorage.get(binaryContentId)) {
      byte[] data = inputStream.readAllBytes();

      return ResponseEntity.ok()
          .header("Content-Type", binaryContentDto.contentType())
          .header("Content-Length", String.valueOf(data.length))
          .header("Content-Disposition", "inline; filename=\"" + binaryContentDto.fileName() + "\"")
          // CORS 헤더 추가 (필요시)
          .header("Access-Control-Allow-Origin", "*")
          .body(data);

    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
