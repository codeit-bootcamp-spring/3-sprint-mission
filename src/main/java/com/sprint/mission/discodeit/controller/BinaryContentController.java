package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @GetMapping(path = "/{binaryContentId}")
  public ResponseEntity<BinaryContentResponseDto> findById(@PathVariable UUID binaryContentId) {
    BinaryContentResponseDto foundBinaryContent = binaryContentService.findById(binaryContentId);

    return ResponseEntity.status(HttpStatus.OK).body(foundBinaryContent);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentResponseDto>> findAll(@RequestBody List<UUID> ids) {
    List<BinaryContentResponseDto> binaryContents = binaryContentService.findAllByIdIn(ids);;

    return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
  }
}
