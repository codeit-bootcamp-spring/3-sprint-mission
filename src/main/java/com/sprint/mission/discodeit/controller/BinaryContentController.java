package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "BinaryContent", description = "첨부파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Operation(summary = "첨부 파일 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "첨부 파일 조회 성공",
          content = @Content(
              mediaType = "*/*",
              schema = @Schema(implementation = BinaryContent.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "첨부 파일 찾을 수 없음",
          content = @Content(
              mediaType = "*/*",
              examples = @ExampleObject("BinaryContent with id {binaryContentId} not found")
          )
      )
  })
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(
      @Parameter(name = "binaryContentId",
          description = "조회할 첨부 파일 ID",
          required = true) @PathVariable("binaryContentId") UUID binaryContentId
  ) {
    BinaryContentDto content = binaryContentService.find(binaryContentId);

    return ResponseEntity.status(HttpStatus.OK).body(content);
  }

  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "첨부 파일 목록 조회 성공",
          content = @Content(
              mediaType = "*/*",
              array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))
          )
      )
  })
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAll(
      @Parameter(name = "binaryContentIds",
          description = "조회할 첨부 파일 ID 목록",
          required = true) @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    List<BinaryContentDto> contents = binaryContentService.findAllByIdIn(binaryContentIds);

    return ResponseEntity.status(HttpStatus.OK).body(contents);
  }

  @Operation(summary = "파일 다운로드")
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "파일 다운로드 성공",
          content = @Content(
              mediaType = "*/*",
              schema = @Schema(type = "string", format = "binary")
          )
      )
  })
  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(
      @Parameter(name = "binaryContentId",
          description = "다운로드할 파일 ID",
          required = true) @PathVariable("binaryContentId") UUID binaryContentId
  ) {
    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

    return binaryContentStorage.download(binaryContent);
  }

}
