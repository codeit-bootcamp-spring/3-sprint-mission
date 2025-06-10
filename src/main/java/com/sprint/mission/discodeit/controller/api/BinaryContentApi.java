package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "BinaryContent", description = "BinaryContent API")
public interface BinaryContentApi {

    @Operation(
        summary = "첨부 파일 조회",
        operationId = "find",
        tags = {"BinaryContent"}
    )
    @ApiResponses(
        value = {
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
        }
    )
    @Parameter(
        name = "binaryContentId",
        in = ParameterIn.PATH,
        description = "조회할 첨부 파일 ID",
        required = true,
        schema = @Schema(type = "string", format = "uuid")
    )
    ResponseEntity<BinaryContentDto> find(UUID binaryContentId);

    @Operation(
        summary = "여러 첨부 파일 조회",
        operationId = "findAllByIdIn",
        tags = {"BinaryContent"}
    )
    @ApiResponse(
        responseCode = "200",
        description = "첨부 파일 목록 조회 성공",
        content = @Content(
            mediaType = "*/*",
            array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))
        )
    )
    @Parameter(
        name = "binaryContentIds",
        in = ParameterIn.QUERY,
        description = "조회할 첨부 파일 ID 목록",
        required = true,
        array = @ArraySchema(schema = @Schema(type = "string", format = "uuid"))
    )
    ResponseEntity<List<BinaryContentDto>> findAll(List<UUID> binaryContentIds);
}
