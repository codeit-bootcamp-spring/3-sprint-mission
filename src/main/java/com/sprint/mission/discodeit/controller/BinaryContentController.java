package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
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
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    //
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(
        @PathVariable UUID binaryContentId
    ) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
        @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(
            binaryContentIds);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentList);
    }

    @GetMapping(path = "/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId)
        throws IOException {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);

        return binaryContentStorage.download(dto);
    }

}
