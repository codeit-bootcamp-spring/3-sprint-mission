package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.mapper.ResponseMapper;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
@Slf4j
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;
  private final MapperFacade mapperFacade;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentResponse> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    log.debug("바이너리 콘텐츠 조회 API 요청 - ID: {}", binaryContentId);

    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    BinaryContentResponse response = ResponseMapper.toResponse(binaryContent);

    log.debug("바이너리 콘텐츠 조회 API 응답 - ID: {}, 파일명: {}, 크기: {} bytes",
        response.id(), response.fileName(), response.size());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    log.debug("다중 바이너리 콘텐츠 조회 API 요청 - ID 개수: {}", binaryContentIds.size());

    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    List<BinaryContentResponse> responses = binaryContents.stream()
        .map(ResponseMapper::toResponse)
        .toList();

    log.debug("다중 바이너리 콘텐츠 조회 API 응답 - 요청: {}, 응답: {}",
        binaryContentIds.size(), responses.size());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(responses);
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
    log.info("파일 다운로드 API 요청 - ID: {}", binaryContentId);

    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    BinaryContentDto binaryContentDto = mapperFacade.toDto(binaryContent);

    log.info("파일 다운로드 시작 - ID: {}, 파일명: {}, 크기: {} bytes",
        binaryContentDto.id(), binaryContentDto.fileName(), binaryContentDto.size());

    ResponseEntity<?> response = binaryContentStorage.download(binaryContentDto);

    log.info("파일 다운로드 완료 - ID: {}, 파일명: {}",
        binaryContentDto.id(), binaryContentDto.fileName());

    return response;
  }
}
