package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
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

/**
 * 바이너리 콘텐츠 관련 HTTP 요청을 처리하는 컨트롤러입니다.
 *
 * <p>특정 바이너리 콘텐츠 조회, 여러 개의 바이너리 콘텐츠 조회,
 * 다운로드 기능을 제공합니다.</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    private static final String CONTROLLER_NAME = "[BinaryContentController] ";

    /**
     * 지정된 ID의 바이너리 콘텐츠를 조회합니다.
     *
     * @param binaryContentId 조회할 바이너리 콘텐츠 ID
     * @return 조회된 바이너리 콘텐츠 DTO
     */
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(
            @PathVariable UUID binaryContentId
    ) {
        log.info(CONTROLLER_NAME + "바이너리 콘텐츠 조회 시도: id={}", binaryContentId);

        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

        log.info(CONTROLLER_NAME + "바이너리 콘텐츠 조회 성공: {}", binaryContent);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContent);
    }

    /**
     * 여러 개의 ID로 바이너리 콘텐츠를 조회합니다.
     *
     * @param binaryContentIds 조회할 바이너리 콘텐츠 ID 목록
     * @return 조회된 바이너리 콘텐츠 엔티티 목록
     */
    @GetMapping
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        log.info(CONTROLLER_NAME + "여러 바이너리 콘텐츠 조회 시도: ids={}", binaryContentIds);

        List<BinaryContent> binaryContentList = binaryContentService.findAllByIdIn(binaryContentIds);

        log.info(CONTROLLER_NAME + "여러 바이너리 콘텐츠 조회 성공: 건수={}", binaryContentList.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(binaryContentList);
    }

    /**
     * 지정된 ID의 바이너리 콘텐츠를 다운로드합니다.
     *
     * @param binaryContentId 다운로드할 바이너리 콘텐츠 ID
     * @return 다운로드 응답
     */
    @GetMapping(path = "/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
        log.info(CONTROLLER_NAME + "바이너리 콘텐츠 다운로드 시도: id={}", binaryContentId);

        BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

        log.info(CONTROLLER_NAME + "바이너리 콘텐츠 다운로드 준비 완료: {}", binaryContent);

        return binaryContentStorage.download(binaryContent);
    }
}
