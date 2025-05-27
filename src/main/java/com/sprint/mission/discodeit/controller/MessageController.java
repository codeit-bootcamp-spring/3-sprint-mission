package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.utils.BinaryContentConverter;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

  private final MessageService messageService;

  /* 메세지 생성 */
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<Message> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments).map(
            p -> p.stream().map(BinaryContentConverter::resolveProfileRequest)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
            // XXX : 왜 null를 return하지 않고 빈 리스트를 반환했지?
        ).orElse(Collections.emptyList());
    try {
      Message createdMessage = messageService.create(messageCreateRequest,
          binaryContentCreateRequests);
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(createdMessage);
    } catch (NoSuchElementException | IllegalAccessException E) {
      return ResponseEntity.unprocessableEntity().build();
    }
  }

  /* 메세지 수정 */
  @PatchMapping(path = "/{messageId}")
  @Override
  public ResponseEntity<Message> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest
  ) {
    Message updatedMessage = messageService.update(messageId,
        messageUpdateRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessage);
  }

  /* 메세지 삭제 */
  @DeleteMapping(path = "/{messageId}")
  @Override
  public ResponseEntity<Void> delete(
      @PathVariable("messageId") UUID messageId
  ) {
    messageService.delete(messageId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  /* 특정 채널의 메시지 목록을 조회 */
  @GetMapping
  @Override
  public ResponseEntity<List<Message>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId
  ) {
    List<Message> MessageList = messageService.findAllByChannelId(channelId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(MessageList);
  }

}
