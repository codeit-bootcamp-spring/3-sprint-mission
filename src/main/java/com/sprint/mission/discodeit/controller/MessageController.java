package com.sprint.mission.discodeit.controller;

import static com.sprint.mission.discodeit.controller.Converter.resolveBinaryContentRequest;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;


  @PostMapping("/messages")
  public ResponseEntity<Message> createMessage(
      @RequestPart("createMessageRequest") CreateMessageRequest request,
      @RequestPart(value = "image", required = false) List<MultipartFile> image) {
    Message message;
    if (image != null && !image.isEmpty()) {
      Optional<List<CreateBinaryContentRequest>> imageRequest = Optional.of(
          resolveBinaryContentRequest(image));
      message = messageService.create(request, imageRequest);
    } else {
      message = messageService.create(request, Optional.empty());
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @GetMapping("/messages")
  public ResponseEntity<List<Message>> searchByChannelId(@RequestParam UUID channelId) {
    List<Message> allByChannelId = messageService.findAllByChannelId(channelId);
    return ResponseEntity.status(HttpStatus.OK).body(allByChannelId);
  }

  @PutMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> updateMessage(
      @RequestPart("updateMessageRequest") UpdateMessageRequest request,
      @RequestPart(value = "image", required = false) List<MultipartFile> image) {
    if (image != null && !image.isEmpty()) {
      Optional<List<CreateBinaryContentRequest>> imageRequest = Optional.of(
          resolveBinaryContentRequest(image));
      messageService.update(request, imageRequest);
    } else {
      messageService.update(request, Optional.empty());
    }
    return ResponseEntity.status(HttpStatus.CREATED).body("변경완료!");
  }

  @DeleteMapping("/messages")
  public ResponseEntity<String> deleteMessage(@RequestParam UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
  }


}
