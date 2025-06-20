package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.utils.BinaryContentConverter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
  private final PageResponseMapper pageResponseMapper;

  /* 메세지 생성 */
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Override
  public ResponseEntity<MessageDto> create(
      @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
      @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
  ) {
    List<BinaryContentCreateRequest> binaryContentCreateRequests =
        Optional.ofNullable(attachments).map(
            p -> p.stream().map(BinaryContentConverter::resolveProfileRequest)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
        ).orElse(new ArrayList<>());

    MessageDto createdMessageDto = messageService.create(messageCreateRequest,
        binaryContentCreateRequests);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdMessageDto);
  }

  /* 메세지 수정 */
  @PatchMapping(path = "/{messageId}")
  @Override
  public ResponseEntity<MessageDto> update(
      @PathVariable("messageId") UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest
  ) {
    MessageDto updatedMessageDto = messageService.update(messageId,
        messageUpdateRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedMessageDto);
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
  public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "cursor", required = false) Instant cursor,
      @PageableDefault(
          size = 50,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      ) Pageable pageable
  ) {
    PageResponse<MessageDto> messages = messageService.findAllByChannelId(channelId, cursor,
        pageable);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(messages);
  }

}
