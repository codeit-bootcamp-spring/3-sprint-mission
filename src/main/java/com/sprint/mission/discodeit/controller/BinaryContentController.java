package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @ApiResponses({
            @ApiResponse(responseCode = "200",  description = "첨부 파일 조회 성공"),
            @ApiResponse(responseCode = "404",  description = "첨부 파일을 찾을 수 없음")
    })
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
    @ApiResponse(responseCode = "200",  description = "첨부 파일 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }
}