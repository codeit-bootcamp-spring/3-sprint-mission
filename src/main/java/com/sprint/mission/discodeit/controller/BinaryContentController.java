package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : BinaryContentController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@Tag(name = "Binary Content 컨트롤러", description = "이미지 파일 정보를 다룹니다.")
@RestController
@RequestMapping("api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Operation(summary = "여러 첨부 파일 조회", description = "여러 첨부파일들을 조회 합니다.")
    @GetMapping
    public ResponseEntity<?> findAttachment(@RequestParam List<UUID> binaryContentIds) {
        return binaryContentService.findAllByIdIn(binaryContentIds);
    }

    @Operation(summary = "단일 첨부 파일 조회", description = "단일 첨부파일을 조회 합니다.")
    @GetMapping(path = "/{binaryContentId}")
    public ResponseEntity<?> findBinaryContent(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }
}
