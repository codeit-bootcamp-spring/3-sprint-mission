package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@RestController
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  // 단건 조회 : 바이너리 파일을 1개 조회( GET )
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(
      @PathVariable UUID binaryContentId
  ) {
    BinaryContent binary = binaryContentService.find(binaryContentId);

    // 조회한 바이너리 파일이 없을 경우
    if (binary == null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 파일의 실제 데이터, 설정된 헤더 정보 포함, 상태코드( 200 ) 반환
    return ResponseEntity.status(HttpStatus.OK).body(binary);
  }

  // 다건 조회 : 바이너리 파일 여러 개 조회( GET )
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

  // 파일 다운로드
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(
      @PathVariable UUID binaryContentId
  ) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);

    BinaryContentDto dtoForDownload = new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        null
    );
    return binaryContentStorage.download(dtoForDownload);
  }
}