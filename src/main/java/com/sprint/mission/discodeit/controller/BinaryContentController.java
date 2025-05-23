package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
@Controller
@ResponseBody
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    /**
     * 바이너리 파일 단건 조회
     */
    @RequestMapping(path = "/find/{binaryContentId}")
    public ResponseEntity<BinaryContent> find(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

    /**
     * 바이너리 파일 다건 조회
     */
    @RequestMapping(path = "/findAll/{binaryContentIds}")
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@PathVariable("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(binaryContents);
    }
}
