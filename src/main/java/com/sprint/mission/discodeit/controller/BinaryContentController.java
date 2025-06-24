package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
    private final BinaryContentStorage binaryContentStorage;

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
    public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
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
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);

    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> fileDownload(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        ResponseEntity<?> response = binaryContentStorage.download(binaryContentDto);

        return response;
    }
}
