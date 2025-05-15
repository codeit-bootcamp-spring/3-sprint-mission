package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/binary-content")
@Controller
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 단일 파일
    @RequestMapping(path = "/find"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> findBinaryContent(
            @RequestParam("id")UUID id
    ) {
        BinaryContent content = binaryContentService.find(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(content.getContentType()));
        headers.setContentLength(content.getSize());
        headers.setContentDisposition(ContentDisposition.attachment().filename(content.getFileName()).build());
        return new ResponseEntity<>(content.getBytes(), headers, HttpStatus.OK);
    }

    // 여러 개 파일
    @RequestMapping(path = "/find-all"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findAllBinaryContent(
            @RequestParam("id") List<UUID> ids
    ) {
        List<BinaryContent> files = binaryContentService.findAllByIdIn(ids);
        List<String> fileNames = files.stream()
                .map(BinaryContent::getFileName)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileNames);
    }
}
