package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @RequestPart("messageRequest") MessageRequestDTO messageRequestDTO,
      @RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
    List<BinaryContentDTO> binaryContentDTOS = resolveFileRequest(attachedFiles);

    Message createdMessage = messageService.create(messageRequestDTO, binaryContentDTOS);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @GetMapping(path = "/{messageId}")
  public ResponseEntity<MessageResponseDTO> findById(@PathVariable UUID messageId) {
    MessageResponseDTO foundMessage = messageService.findById(messageId);

    return ResponseEntity.status(HttpStatus.OK).body(foundMessage);
  }

  @GetMapping(path = "/channels")
  public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(@RequestParam UUID channelId) {
    List<MessageResponseDTO> foundMessages = messageService.findAllByChannelId(channelId);

    return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
  }

  @GetMapping
  public ResponseEntity<List<MessageResponseDTO>> findAll() {
    List<MessageResponseDTO> foundMessages = messageService.findAll();

    return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
  }

  @GetMapping(path = "/users")
  public ResponseEntity<List<MessageResponseDTO>> findAllByUserId(@RequestParam UUID userId) {
    List<MessageResponseDTO> foundMessages = messageService.findAllByUserId(userId);

    return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
  }

  @GetMapping(path = "/word")
  public ResponseEntity<List<MessageResponseDTO>> findAllByContainingWord(
      @RequestParam String word) {
    List<MessageResponseDTO> foundMessages = messageService.findAllByContainingWord(word);

    return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
  }

  @PatchMapping(path = "/{messageId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponseDTO> updateBinaryContent(@PathVariable UUID messageId,
      @RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
    List<BinaryContentDTO> binaryContentDTOS = resolveFileRequest(attachedFiles);

    MessageResponseDTO updatedMessage = messageService.updateBinaryContent(messageId,
        binaryContentDTOS);

    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @PatchMapping(path = "/{messageId}")
  public ResponseEntity<MessageResponseDTO> updateContent(@PathVariable UUID messageId,
      String content) {
    MessageResponseDTO updatedMessage = messageService.updateContent(messageId, content);

    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID messageId) {
    messageService.deleteById(messageId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private List<BinaryContentDTO> resolveFileRequest(List<MultipartFile> attachedFiles) {
    if (attachedFiles == null || attachedFiles.isEmpty()) {
      return Collections.emptyList();
    }

    List<BinaryContentDTO> binaryContentList = new ArrayList<>();
    for (MultipartFile file : attachedFiles) {
      if (file.isEmpty()) {
        continue;
      }
      try {
        binaryContentList.add(new BinaryContentDTO(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getBytes()
        ));
      } catch (IOException e) {
        throw new RuntimeException("파일 '" + file.getOriginalFilename() + "' 처리 중 오류 발생", e);
      }
    }

    return binaryContentList;
  }
}
