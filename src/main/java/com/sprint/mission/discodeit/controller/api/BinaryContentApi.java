package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "BinaryContent", description = "첨부파일 API")
public interface BinaryContentApi {

  @Operation(summary = "첨부 파일 조회")
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
          @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
              content = @Content(examples = {
                  @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")}))
      }
  )
  ResponseEntity<BinaryContentResponseDto> findById(
      @Parameter(description = "조회할 첨부 파일 ID") @PathVariable UUID binaryContentId);

  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
  ResponseEntity<List<BinaryContentResponseDto>> findAll(
      @Parameter(description = "조회할 첨부 파일 ID 목록") @RequestBody(required = false) List<UUID> ids);
}
