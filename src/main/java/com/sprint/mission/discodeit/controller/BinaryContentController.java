package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.mapper.EntityDtoMapper;
import com.sprint.mission.discodeit.dto.mapper.ResponseMapper;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final EntityDtoMapper entityDtoMapper;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentResponse> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    BinaryContentResponse response = ResponseMapper.toResponse(binaryContent);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    List<BinaryContentResponse> responses = binaryContents.stream()
        .map(ResponseMapper::toResponse)
        .toList();
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(responses);
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    BinaryContentDto binaryContentDto = entityDtoMapper.toDto(binaryContent);
    return binaryContentStorage.download(binaryContentDto);
  }
}
