package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CodeMessageResponseDto;
import com.sprint.mission.discodeit.dto.ResponseCode;
import com.sprint.mission.discodeit.dto.ResponseMessage;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Tag(
        name = "BinaryContent",
        description = "BinaryContent API"
)
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(
          summary = "첨부 파일 조회",
          operationId = "find"
  )
  @ApiResponses(value = {
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
                  description = "첨부 파일을 찾을 수 없음",
                  content = @Content(
                          mediaType = "*/*",
                          examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")
                  )
          )
  })
  @Parameter(
          name = "binaryContentId",
          description = "조회할 첨부 파일 ID",
          required = true,
          in = ParameterIn.PATH,
          schema = @Schema(type = "string", format = "uuid")
  )
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<?> find(@PathVariable UUID binaryContentId) {
      try {
          BinaryContent binaryContent = binaryContentService.find(binaryContentId);
          return ResponseEntity
                  .status(HttpStatus.OK)
                  .body(binaryContent);
      } catch (NoSuchElementException e) {
          return ResponseEntity
                  .status(HttpStatus.NOT_FOUND)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.BINARY_NOT_FOUND,
                          ResponseMessage.BINARY_NOT_FOUND
                  ));
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.INTERNAL_ERROR,
                          ResponseMessage.INTERNAL_SERVER_ERROR
                  ));
      }
  }

  @Operation(
          summary = "여러 첨부 파일 조회",
          operationId = "findAllByIdIn"
  )
  @ApiResponse(
          responseCode = "200",
          description = "첨부 파일 목록 조회 성공",
          content = @Content(
                  mediaType = "*/*",
                  array = @ArraySchema(
                          schema = @Schema(implementation = BinaryContent.class)
                  )
          )
  )
  @Parameter(
          name = "binaryContentId",
          description = "조회할 첨부 파일 ID 목록",
          required = true,
          in = ParameterIn.QUERY,
          array = @ArraySchema(schema = @Schema(type = "string", format = "uuid"))
  )
  @GetMapping("")
  public ResponseEntity<?> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
      try {
          List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
          return ResponseEntity
                  .status(HttpStatus.OK)
                  .body(binaryContents);
      } catch (RuntimeException e) {
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(CodeMessageResponseDto.error(
                          ResponseCode.INTERNAL_ERROR,
                          ResponseMessage.INTERNAL_SERVER_ERROR
                  ));
      }

  }
}
