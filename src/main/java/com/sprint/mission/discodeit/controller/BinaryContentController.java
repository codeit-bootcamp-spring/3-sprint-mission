package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @RequestMapping("/find")
  public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId")UUID binaryContentId) {
    BinaryContent file = binaryContentService.findById(binaryContentId)
        .orElseThrow(() -> new NoSuchElementException("파일 조회에 실패하였습니다.: " + binaryContentId));

    return ResponseEntity.status(HttpStatus.OK).body(file);
  }

  @RequestMapping("/findAll")
  public ResponseEntity<List<BinaryContent>> findAll(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> files = binaryContentService.findAllByIdIn(binaryContentIds);

    return ResponseEntity.status(HttpStatus.OK).body(files);
  }
}
