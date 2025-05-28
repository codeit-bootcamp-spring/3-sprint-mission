package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
public interface BinaryContentApi {

  @Operation(summary = "첨부 파일 조회 단건")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
      @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음")
  })
  ResponseEntity<BinaryContent> find(UUID binaryContentId);

  @Operation(summary = "첨부 파일 조회 다건")
  @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공")
  ResponseEntity<List<BinaryContent>> findAllByIdIn(List<UUID> binaryContentIds);
}
