package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.MessageApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.FileConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> create(
            @Valid @RequestPart("messageCreateRequest") MessageRequestDto messageRequestDTO,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachedFiles) {
        List<BinaryContentDto> binaryContentDtos = FileConverter.resolveFileRequest(attachedFiles);

        MessageResponseDto createdMessage = messageService.create(messageRequestDTO, binaryContentDtos);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @GetMapping
    public ResponseEntity<PageResponse<MessageResponseDto>> findAllByChannelId(@RequestParam UUID channelId,
                                                                               @RequestParam(required = false) Instant cursor,
                                                                               @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                               Pageable pageable) {

        PageResponse<MessageResponseDto> foundMessages = messageService.findAllByChannelId(channelId, cursor, pageable);

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
