package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/message")
@Controller
public class MessageController {
    private final MessageService messageService;

    // 메시지 송신
    @RequestMapping(
            path = "/create"
            , method = RequestMethod.POST
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        List<BinaryContentCreateRequest> binaryRequests = Optional.ofNullable(attachments)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toOptionalBinaryContentRequest)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        Message message = messageService.create(messageCreateRequest, binaryRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // 메시지 수정
    @RequestMapping(path = "/update"
            , method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Message> update(
            @RequestParam("id") UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        Message updated = messageService.update(messageId, messageUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    // 메시지 삭제
    @RequestMapping(path = "/delete"
            , method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(
            @RequestParam("id") UUID messageId
    ) {
        messageService.delete(messageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 채널 메시지 목록 조회
    @RequestMapping(path = "/find-all-by-channel"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Message>> findAllByChannel(
            @RequestParam("channelId") UUID channelId
    ) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변경하기 위한 메서드
    private Optional<BinaryContentCreateRequest> toOptionalBinaryContentRequest(MultipartFile profile) {

        if (profile.isEmpty()) {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 비어있다면:
            return Optional.empty();
        } else {
            // 컨트롤러가 요청받은 파라미터 중 MultipartFile 타입의 데이터가 존재한다면:
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        profile.getOriginalFilename(),
                        profile.getBytes(),
                        profile.getContentType()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
