package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping(path = "{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
        @PathVariable("binaryContentId") UUID binaryContentId) {

        log.info("[BinaryContentController] Find request received. [binaryContentId={}]",
            binaryContentId);

        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

        log.debug("[BinaryContentController] Binary content found. [id={}]", binaryContent.id());
        return ResponseEntity.status(HttpStatus.OK).body(binaryContent);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
        @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {

        log.info("[BinaryContentController] Batch find request received. [ids={}]",
            binaryContentIds);

        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(
            binaryContentIds);

        log.debug("[BinaryContentController] Binary contents found. [count={}]",
            binaryContents.size());
        return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
    }

    @GetMapping(path = "{binaryContentId}/download")
    public ResponseEntity<?> download(
        @PathVariable("binaryContentId") UUID binaryContentId) {

        log.info("[BinaryContentController] Download request received. [binaryContentId={}]",
            binaryContentId);

        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

        log.debug("[BinaryContentController] Downloading binary content. [id={}]",
            binaryContentDto.id());
        return binaryContentStorage.download(binaryContentDto);
    }
}
