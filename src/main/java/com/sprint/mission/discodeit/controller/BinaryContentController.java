package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
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

    @GetMapping("/{binaryContentId}")
    @Override
    public ResponseEntity<BinaryContentResponse> find(
        @PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent file = binaryContentService.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일 조회에 실패하였습니다.: " + binaryContentId));
        BinaryContentResponse response = BinaryContentResponse.fromEntity(file);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<BinaryContentResponse>> findAll(
        @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> files = binaryContentService.findAllByIdIn(binaryContentIds);
        List<BinaryContentResponse> responses = files.stream()
            .map(BinaryContentResponse::fromEntity)
            .toList();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(responses);
    }
}