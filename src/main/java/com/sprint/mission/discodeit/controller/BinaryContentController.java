package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BinaryContent", description = "첨부파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(summary = "첨부 파일 조회")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
          @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
              content = @Content(examples = {
                  @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")}))
      }
  )
  @GetMapping(path = "/{binaryContentId}")
  public ResponseEntity<BinaryContentResponseDTO> findById(
      @Parameter(description = "조회할 첨부 파일 ID") @PathVariable UUID binaryContentId) {
    BinaryContentResponseDTO foundBinaryContent = binaryContentService.findById(binaryContentId);

    return ResponseEntity.status(HttpStatus.OK).body(foundBinaryContent);
  }

  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
  @GetMapping
  public ResponseEntity<List<BinaryContentResponseDTO>> findAll(
      @Parameter(description = "조회할 첨부 파일 ID 목록") @RequestBody(required = false) List<UUID> ids) {
    List<BinaryContentResponseDTO> binaryContents;

    if (ids == null) {
      binaryContents = binaryContentService.findAll();
    } else {
      binaryContents = binaryContentService.findAllByIdIn(ids);
    }

    return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
  }
}
