package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Tag(name = "첨부 파일", description = "첨부 파일 API")
@RequestMapping("/api/binaryContents")
@RestController
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(
      summary = "첨부 파일 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공", content = @Content(schema = @Schema(implementation = BinaryContent.class))),
          @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
      }
  )
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> findBinaryContentById(
      @Parameter(description = "조회할 첨부 파일의 ID", required = true)
      @PathVariable("binaryContentId") UUID contentId
  ) {
    BinaryContent binaryContent = binaryContentService.findById(contentId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }


  @Operation(
      summary = "첨부 파일 다중 조회"
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))))
      }
  )
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllBinaryContentByIds(
      @Parameter(description = "조회할 첨부 파일들의 ID 목록", required = true)
      @RequestParam("binaryContentIds") List<UUID> contentIds
  ) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(contentIds);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }

}
