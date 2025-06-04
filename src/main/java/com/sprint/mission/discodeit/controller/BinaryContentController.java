package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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

@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@RestController
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    /**
     * 파이너리 파일 단건 조회
     *
     * @param binaryContentId 조회할 바이너리 파일 ID
     * @return 조회된 바이너리 파일 (HTTP 200 OK)
     */
    @GetMapping(path = "/{binaryContentId}")
    @Override
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDto content = binaryContentService.find(binaryContentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(content);
    }

    /**
     * 바이너리 파일 1개 또는 여러 개 조회
     *
     * @param binaryContentIds 조회할 바이너리 파일 ID 목록
     * @return 조회된 바이너리 파일 목록 (HTTP 200 OK)
     */
    @GetMapping
    @Override
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> contents = binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contents);
    }

    /**
     * 바이너리 파일 다운로드
     *
     * @param binaryContentId 다운로드할 바이너리 파일 ID
     * @return 파일 다운로드 응답
     */
    @GetMapping(path = "/{binaryContentId}/download")
    @Override
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {

        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

        return binaryContentStorage.download(binaryContentDto);
    }
}
