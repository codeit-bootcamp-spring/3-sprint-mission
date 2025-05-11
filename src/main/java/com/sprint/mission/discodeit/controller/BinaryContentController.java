package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.annotation.Resource;
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
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<BinaryContent> create(@RequestPart(value = "file") MultipartFile multipartFile) {
        BinaryContentDTO binaryContentDTO = resolveFileRequest(multipartFile);

        BinaryContent binaryContent = binaryContentService.create(binaryContentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(binaryContent);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BinaryContentResponseDTO> findById(@RequestParam UUID binaryContentId) {
        BinaryContentResponseDTO foundBinaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.status(HttpStatus.OK).body(foundBinaryContent);
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContentResponseDTO>> findAll() {
        List<BinaryContentResponseDTO> allBinaryContents = binaryContentService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(allBinaryContents);
    }

    @RequestMapping(path = "/findAllByIdIn", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContentResponseDTO>> findAllByIdIn(@RequestBody List<UUID> ids) {
        List<BinaryContentResponseDTO> foundBinaryContents = binaryContentService.findAllByIdIn(ids);

        return ResponseEntity.status(HttpStatus.OK).body(foundBinaryContents);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteById(@RequestParam UUID binaryContentId) {
        binaryContentService.deleteById(binaryContentId);

        return ResponseEntity.status(HttpStatus.CREATED).body("[Success]: 파일 삭제 성공!");
    }

    private BinaryContentDTO resolveFileRequest(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return null;
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentDTO binaryContentDTO = new BinaryContentDTO(
                        multipartFile.getOriginalFilename(),
                        multipartFile.getContentType(),
                        multipartFile.getBytes());

                return binaryContentDTO;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
