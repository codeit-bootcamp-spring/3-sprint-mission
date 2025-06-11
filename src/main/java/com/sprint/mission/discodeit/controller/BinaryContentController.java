package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
    private final BinaryContentStorage storage;

    @GetMapping(path = "/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto content = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(content);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDto> list = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(list);
    }

    @GetMapping(path = "/{binaryContentId}/download")
    public ResponseEntity<?> download(
            @PathVariable("binaryContentId") UUID binaryContentId) throws IOException {

        BinaryContentDto meta = binaryContentService.find(binaryContentId);

        return storage.download(meta);
    }
}