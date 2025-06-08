package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping("/{binaryContentId}")
    @Override
    public ResponseEntity<BinaryContentDto> find(
        @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto file = binaryContentService.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일 조회에 실패하였습니다.: " + binaryContentId));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(file);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<BinaryContentDto>> findAll(
        @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentDto> files = binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(files);
    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일 조회에 실패하였습니다.: " + binaryContentId));

        ResponseEntity<?> response = binaryContentStorage.download(binaryContentDto);

        return response;
    }
}