package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<ByteArrayResource> find(@PathVariable("id") UUID id) {
        BinaryContent content = binaryContentService.find(id)
                .orElseThrow(() -> new RuntimeException("해당 파일이 존재하지 않습니다."));

        String encodedFileName = UriUtils.encode(content.getFileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFileName)
                .body(new ByteArrayResource(content.getBytes()));
    }

    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
    }

    @GetMapping(path = "/api/binaryContent/find")
    public ResponseEntity<BinaryContent> findByApi(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent content = binaryContentService.find(binaryContentId)
                .orElseThrow(() -> new RuntimeException("해당 BinaryContent가 존재하지 않습니다."));
        return ResponseEntity.ok(content);
    }
}
