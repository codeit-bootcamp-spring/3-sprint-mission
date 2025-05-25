package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentMetadataDto;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Controller
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 첨부 파일 조회
    @RequestMapping(path = "/{binaryContentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> findBinaryContent(@PathVariable("binaryContentId") UUID binaryContentId) {
        try {
            BinaryContent binaryContent = binaryContentService.findById(binaryContentId);
            return ResponseEntity.ok(binaryContent);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("BinaryContent with id " + binaryContentId + " not found");
        }
    }

    // 여러 첨부 파일 조회
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<BinaryContentMetadataDto>> findAllBinaryContentMetadataByIds(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIds(binaryContentIds);
        List<BinaryContentMetadataDto> metadataDtos = binaryContents.stream()
                .map(content -> new BinaryContentMetadataDto(
                        content.getId(),
                        content.getCreatedAt(),
                        content.getFileName(),
                        content.getSize(),
                        content.getContentType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(metadataDtos);
    }
}
