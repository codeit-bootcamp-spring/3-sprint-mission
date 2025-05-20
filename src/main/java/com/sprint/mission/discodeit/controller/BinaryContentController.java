package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @GetMapping("/{id}")
    public ResponseEntity<BinaryContentDTO> get(@PathVariable UUID id) throws IOException {
        BinaryContent binaryContent = binaryContentService.find(id);

        Path file = Paths.get("src/main/resources/static" + binaryContent.getPath());
        String contentType = Files.probeContentType(file);
        String base64     = Base64.getEncoder().encodeToString(Files.readAllBytes(file));
        return ResponseEntity.ok(new BinaryContentDTO(contentType, base64));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BinaryContent>> getAll() {
        List<BinaryContent> binaryContents = binaryContentService.findAll();
        return ResponseEntity.ok(binaryContents);
    }
}