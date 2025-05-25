package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContents")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Operation(summary = "binary 파일 생성")
    @ApiResponse(responseCode = "201",  description = "생성 성공")
    @GetMapping("/{id}")
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    @Operation(summary = "전체 파일 조회")
    @ApiResponse(responseCode = "200",  description = "조회 성공")
    @GetMapping("/all")
    public ResponseEntity<List<BinaryContent>> getAll() {
        List<BinaryContent> binaryContents = binaryContentService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }
}