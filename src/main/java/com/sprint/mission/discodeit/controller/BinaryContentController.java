package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@RestController
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;

    // 단일 파일 조회
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(
            @PathVariable("binaryContentId") UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    // 다중 파일 조회
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }
}
