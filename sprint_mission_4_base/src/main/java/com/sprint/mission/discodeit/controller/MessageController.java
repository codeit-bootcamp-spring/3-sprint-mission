package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/message")
@Controller
public class MessageController {

    private final MessageService messageService;

    // 신규 메시지 생성
    @RequestMapping(
            path = "/create"
//            , method = RequestMethod.POST
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public ResponseEntity<Message> create(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {

        List<BinaryContentCreateRequest> binaryContents = resolveAttachmentRequest(attachments);
        Message message = messageService.create(messageCreateRequest, binaryContents);

        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private List<BinaryContentCreateRequest> resolveAttachmentRequest(List<MultipartFile> attachments) {
        if (attachments == null || attachments.isEmpty()) return List.of();

        return attachments.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes()
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("파일 처리 중 오류 발생", e);
                    }
                })
                .toList();
    }

    // 메시지 수정
    @RequestMapping(
            path = "/update"
//            , method = RequestMethod.PUT
    )
    @ResponseBody
    public ResponseEntity<Message> update(
            @RequestParam UUID messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        Message updatedMessage = messageService.update(messageId, messageUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    // 메시지 삭제
    @RequestMapping(
            path = "/delete"
//            , method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> delete(
            @RequestParam UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("메시지가 성공적으로 삭제되었습니다.");
    }

    // 특정 채널 메시지 목록 조회
    @RequestMapping(
            path = "/findAllByChannelId"
//            , method = RequestMethod.GET
    )
    @ResponseBody
    public ResponseEntity<List<Message>> findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        List<Message> messageList = messageService.findAllByChannelId(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

}


