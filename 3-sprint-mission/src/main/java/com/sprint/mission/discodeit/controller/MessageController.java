package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
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
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/message")
@ResponseBody
@Controller
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(
            value = "/create"
            , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> create(
            @RequestPart(value = "messageCreateDTO") MessageCreateRequest messageCreateDTO
            , @RequestPart(value = "attachments", required = false) MultipartFile[] files
    ) {

        List<BinaryContentCreateRequest> attachments =
                resolveBinaryContentRequest(files);

        Message createdMessage = messageService.create(messageCreateDTO, attachments);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    // MultipartFile 타입의 요청값을 BinaryContentCreateRequest 타입으로 변환하기 위한 메서드
    private List<BinaryContentCreateRequest> resolveBinaryContentRequest(MultipartFile[] files) {

        List<BinaryContentCreateRequest> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    BinaryContentCreateRequest dto = new BinaryContentCreateRequest(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            file.getBytes()
                    );

                    attachments.add(dto);
                } catch (IOException e) {
                    throw new RuntimeException("파일 처리 중 오류 발생: " + file.getOriginalFilename(), e);
                }
            }
        }

        return attachments;

    }

    // 메시지 다건 조회
    @RequestMapping(
            value = "/findAll",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Message>> findAll(
            @RequestParam UUID channelId
    ) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.ok(messages);
    }

    // 메시지 수정
    @RequestMapping(
            value = "/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Message> update(
            @RequestParam UUID messageId
            , @RequestBody MessageUpdateRequest messageUpdateDTO
    ) {

        Message createdMessage = messageService.update(messageId, messageUpdateDTO);

        return ResponseEntity.ok(createdMessage);
    }

    // 메시지 삭제
    @RequestMapping(
            value = "/delete"
            , method = RequestMethod.DELETE
//            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> delete(
            @RequestParam UUID messageId
    ) {
        messageService.delete(messageId);

        return ResponseEntity.ok("메시지를 삭제했습니다.");
    }
}