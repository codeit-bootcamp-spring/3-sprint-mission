package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.FileConverter;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageRequestDto messageRequestDTO,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachedFiles) {
    List<BinaryContentDto> binaryContentDtos = FileConverter.resolveFileRequest(attachedFiles);

    Message createdMessage = messageService.create(messageRequestDTO, binaryContentDtos);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
  }

  @GetMapping
  public ResponseEntity<List<MessageResponseDto>> findAllByChannelId(@RequestParam UUID channelId) {
    List<MessageResponseDto> foundMessages = messageService.findAllByChannelId(channelId);

    return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
  }

  @PatchMapping(path = "/{messageId}")
  public ResponseEntity<MessageResponseDto> updateContent(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateDto messageUpdateDTO) {
    MessageResponseDto updatedMessage = messageService.updateContent(messageId,
        messageUpdateDTO.newContent());

    return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
  }

  @DeleteMapping(path = "/{messageId}")
  public ResponseEntity<String> deleteById(@PathVariable UUID messageId) {
    messageService.deleteById(messageId);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
