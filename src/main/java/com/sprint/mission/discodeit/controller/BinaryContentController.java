package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping(path = "/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent content = binaryContentService.find(binaryContentId)
                .orElseThrow(() -> new RuntimeException("해당 파일이 존재하지 않습니다."));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(content);

    }

    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContents);
    }

    @GetMapping(path = "/api/binaryContent/find")
    public ResponseEntity<BinaryContent> findByApi(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent content = binaryContentService.find(binaryContentId)
                .orElseThrow(() -> new RuntimeException("해당 BinaryContent가 존재하지 않습니다."));
        return ResponseEntity.ok(content);
    }
}
