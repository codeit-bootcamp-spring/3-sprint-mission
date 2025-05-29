package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "BinaryContent API")
public interface BinaryContentApi {

        @Operation(summary = "BinaryContent 메타데이터 조회")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "BinaryContent 메타데이터 조회 성공", content = @Content(schema = @Schema(implementation = BinaryContentResponse.class))),
                        @ApiResponse(responseCode = "404", description = "BinaryContent를 찾을 수 없음", content = @Content(examples = @ExampleObject("BinaryContent with id {binaryContentId} not found")))
        })
        ResponseEntity<BinaryContentResponse> find(
                        @Parameter(description = "BinaryContent ID") UUID binaryContentId);

        @Operation(summary = "여러 BinaryContent 메타데이터 조회")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "BinaryContent 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentResponse.class))))
        })
        ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
                        @Parameter(description = "BinaryContent ID 목록") List<UUID> binaryContentIds);
}