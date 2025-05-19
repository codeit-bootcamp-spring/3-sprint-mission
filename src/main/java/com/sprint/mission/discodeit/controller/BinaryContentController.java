package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> find(@PathVariable("id") UUID id) {
        BinaryContent content = binaryContentService.find(id)
                .orElseThrow(() -> new RuntimeException("해당 파일이 존재하지 않습니다."));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + content.getFileName() + "\"")
                .body(new ByteArrayResource(content.getBytes()));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContent>> findAllByIdIn(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/api/binaryContent/find"
    )
    @ResponseBody
    public ResponseEntity<BinaryContent> findByApi(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent content = binaryContentService.find(binaryContentId)
                .orElseThrow(() -> new RuntimeException("해당 BinaryContent가 존재하지 않습니다."));
        return ResponseEntity.ok(content);
    }
}
