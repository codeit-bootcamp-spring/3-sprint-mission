package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @PostMapping
  public ResponseEntity<BinaryContent> create(
      @RequestPart(value = "file") MultipartFile multipartFile) {
    BinaryContentDTO binaryContentDTO = resolveFileRequest(multipartFile);

    BinaryContent binaryContent = binaryContentService.create(binaryContentDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(binaryContent);
  }

  @GetMapping(path = "/{binaryContentId}")
  public ResponseEntity<BinaryContentResponseDTO> findById(@PathVariable UUID binaryContentId) {
    BinaryContentResponseDTO foundBinaryContent = binaryContentService.findById(binaryContentId);

    return ResponseEntity.status(HttpStatus.OK).body(foundBinaryContent);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentResponseDTO>> findAll(
      @RequestBody(required = false) List<UUID> ids) {
    List<BinaryContentResponseDTO> binaryContents;

    if (ids == null) {
      binaryContents = binaryContentService.findAll();
    } else {
      binaryContents = binaryContentService.findAllByIdIn(ids);
    }

    return ResponseEntity.status(HttpStatus.OK).body(binaryContents);
  }

  @DeleteMapping("/{binaryContentId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID binaryContentId) {
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
        throw new RuntimeException("파일 '" + multipartFile.getOriginalFilename() + "' 처리 중 오류 발생",
            e);
      }
    }
  }
}
