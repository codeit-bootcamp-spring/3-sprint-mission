package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
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

    // 단일 파일
    @RequestMapping(path = "/find"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BinaryContent> findBinaryContent(
            @RequestParam("binaryContentId")UUID id
    ) {
        BinaryContent content = binaryContentService.find(id);
        return ResponseEntity.ok(content);
    }

    // 여러 개 파일
    @RequestMapping(path = "/findAllBinaryContentIds"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findAllBinaryContent(
            @RequestParam("binaryContentIds") List<UUID> ids
    ) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(contents);
    }
}