package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "BinaryContent", description = "첨부 파일 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
            content = @Content(schema = @Schema(implementation = BinaryContent.class))),
        @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "BinaryContent with id {binaryContentId} not found")))
    })
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(
        @Parameter(description = "조회할 첨부 파일 ID") @PathVariable UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);

        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
        @Parameter(description = "조회할 첨부 파일 ID 목록")
        @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(
            binaryContentIds);

        return ResponseEntity.status(HttpStatus.OK).body(binaryContentList);
    }
}
