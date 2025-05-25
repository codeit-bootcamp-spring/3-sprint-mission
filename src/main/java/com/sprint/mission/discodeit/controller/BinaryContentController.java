package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  // 다건 조회 : 바이너리 파일 여러 개 조회( GET )
  @Operation(
      summary = "다건 바이너리 파일 조회",
      description = "UUID 리스트를 이용하여 여러 개의 바이너리 콘텐츠 정보를 조회합니다"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "바이너리 콘텐츠 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContent.class))
      ),
      @ApiResponse(
          responseCode = "204",
          description = "요청된 ID 리스트가 없거나 해당 ID들에 대한 콘텐츠가 존재하지 않음"
      )
  })
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findBinaryContents(
      @Parameter(description = "조회할 바이너리 콘텐츠의 UUID 목록", required = true, example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }


  // 단건 조회 : 바이너리 파일을 1개 조회( GET )
  @Operation(
      summary = "단건 바이너리 파일 조회",
      description = "UUID를 이용하여 하나의 바이너리 콘텐츠 정보를 조회합니다"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "바이너리 콘텐츠 조회 성공",
          content = @Content(schema = @Schema(implementation = BinaryContent.class))
      ),
      @ApiResponse(
          responseCode = "204",
          description = "해당 ID의 콘텐츠가 존재하지 않음"
      )
  })
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> findBinaryContent(
      @Parameter(description = "조회할 바이너리 콘텐츠의 UUID", required = true, example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
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
}