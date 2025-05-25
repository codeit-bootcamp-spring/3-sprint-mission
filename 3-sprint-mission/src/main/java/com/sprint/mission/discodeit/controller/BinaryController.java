package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
@ResponseBody
@Controller
public class BinaryController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentRepository binaryContentRepository;

    @RequestMapping(
            value = "/findAll"
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<BinaryContent>> findAll() {

        List<BinaryContent> result =
                binaryContentRepository.findAll();

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @RequestMapping(
            value = "/find"
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Optional<BinaryContent>> find(
//            @PathVariable UUID fileId
            @RequestParam UUID binaryContentId
    ) {

        Optional<BinaryContent> result =
                binaryContentRepository.findById(binaryContentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private List<BinaryContentCreateRequest> resolveBinaryContentRequest(MultipartFile[] files) {

        List<BinaryContentCreateRequest> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    BinaryContentCreateRequest dto = new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    );

                    attachments.add(dto);
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 중 오류 발생: " + file.getOriginalFilename(), e);
                }
            }
        }

        return attachments;

    }
}
