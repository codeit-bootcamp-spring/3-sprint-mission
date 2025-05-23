package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/api/binaryContent")
@Controller
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 바이너리 파일 조회 (심화 요구사항: BinaryContent 객체 반환)
    @RequestMapping(
            path = "/find",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<BinaryContent> findBinaryContent(@RequestParam("binaryContentId") UUID binaryContentId) {
        try {
            BinaryContent binaryContent = binaryContentService.find(binaryContentId);
            return ResponseEntity.ok(binaryContent);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 여러 바이너리 파일 메타데이터 조회(여러 이미지 파일 ID로 해당되는 이미지 조회)
    @RequestMapping(
            path = "/findAllMetadata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<BinaryContentMetadataDto>> findAllBinaryContentMetadataByIds(@RequestParam("ids") List<UUID> ids) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(ids);
        List<BinaryContentMetadataDto> metadataDtos = binaryContents.stream()
                .map(content -> new BinaryContentMetadataDto(
                        content.getId(),
                        content.getCreatedAt(),
                        content.getFileName(),
                        content.getSize(),
                        content.getContentType()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(metadataDtos);
    }
}
