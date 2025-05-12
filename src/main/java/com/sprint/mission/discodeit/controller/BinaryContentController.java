package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
@Controller
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    /**
     * 파이너리 파일 단건 조회
     *
     * @param binaryContentId 조회할 바이너리 파일 ID
     * @return 조회된 바이너리 파일 (HTTP 200 OK)
     */
    @RequestMapping(
            path = "/find",
            method = RequestMethod.GET
    )
    public ResponseEntity<BinaryContent> find (
            @RequestParam UUID binaryContentId
    ) {
        // 바이너리 파일 조회
        BinaryContent content = binaryContentService.find(binaryContentId);

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
    @RequestMapping(
            path = "/findAllByIdIn",
            method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findAllByIdIn (
            @RequestParam List<UUID> binaryContentIds
    ) {
        // ID 목록에 해당되는 바이너리 파일 전체 조회
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contents);
    }
}
