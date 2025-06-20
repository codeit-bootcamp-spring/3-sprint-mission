package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.unit.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : BinaryContentController
 * author         : doungukkim
 * date           : 2025. 5. 11.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 11.        doungukkim       최초 생성
 */
@RestController
@RequestMapping("api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping
    public ResponseEntity<?> findAttachment(@RequestParam List<UUID> binaryContentIds) {
        List<JpaBinaryContentResponse> responses = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(path = "/{binaryContentId}")
    public ResponseEntity<?> findBinaryContent(@PathVariable UUID binaryContentId) {
        JpaBinaryContentResponse response = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping(path = "/{binaryContentId}/download")
    public ResponseEntity<?> downloadBinaryContent(@PathVariable UUID binaryContentId) {
        JpaBinaryContentResponse binaryContentResponse = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(binaryContentResponse);
    }
}
