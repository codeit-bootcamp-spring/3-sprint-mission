package com.sprint.mission.discodeit.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.controller.api
 * FileName     : BinaryContentApi
 * Author       : dounguk
 * Date         : 2025. 6. 19.
 */
@Tag(name = "Binary Content 컨트롤러", description = "이미지 파일 정보를 다룹니다.")
@RequestMapping("api/binaryContents")
public interface BinaryContentApi {

    @Operation(summary = "여러 첨부 파일 조회", description = "여러 첨부파일들을 조회 합니다.")
    @GetMapping
    ResponseEntity<?> findAttachment(@RequestParam List<UUID> binaryContentIds);

    @Operation(summary = "단일 첨부 파일 조회", description = "단일 첨부파일을 조회 합니다.")
    @GetMapping("/{binaryContentId}")
    ResponseEntity<?> findBinaryContent(@PathVariable UUID binaryContentId);

    @Operation(summary = "첨부파일 다운로드", description = "단일 첨부파일을 다운 합니다.")
    @GetMapping("/{binaryContentId}/download")
    ResponseEntity<?> downloadBinaryContent(@PathVariable UUID binaryContentId);
}