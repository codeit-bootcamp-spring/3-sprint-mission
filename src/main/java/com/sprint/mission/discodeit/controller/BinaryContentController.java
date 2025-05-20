package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable UUID id) {
        Optional<BinaryContent> fileOpt = binaryContentService.findById(id);
        if (fileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BinaryContent file = fileOpt.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFilename()).build());

        return new ResponseEntity<>(file.getData(), headers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFiles() {
        List<Map<String, Object>> files = binaryContentService.findAll().stream()
                .map(file -> Map.of(
                        "id", file.getId(),
                        "filename", file.getFilename(),
                        "contentType", file.getContentType(),
                        "size", file.getData().length,
                        "createdAt", file.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }
}