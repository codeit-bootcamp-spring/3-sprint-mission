package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    /* 메세지 생성 */
    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity<Message> createMessage(
            @RequestPart("messageCreateRequest") MessageCreateRequest messageCreateRequest,
            @RequestPart(value = "profiles", required = false) List<MultipartFile> profiles
    ) {
        List<BinaryContentCreateRequest> binaryContentCreateRequests =
                Optional.ofNullable(profiles).map(
                        p -> p.stream().map(this::resolveProfileRequest)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList()
                ).orElse(Collections.emptyList());
        try {
            Message createdMessage = messageService.create(messageCreateRequest, binaryContentCreateRequests);
            return ResponseEntity.created(URI.create(createdMessage.getId().toString())).body(createdMessage);
        } catch (NoSuchElementException | IllegalAccessException E) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    /* 메세지 수정 */
    @RequestMapping(path = "/{messageId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Message> update(
            @PathVariable String messageId,
            @RequestBody MessageUpdateRequest messageUpdateRequest
    ) {
        Message updatedMessage = messageService.update(parseStringToUuid(messageId), messageUpdateRequest);
        return ResponseEntity.ok().body(updatedMessage);
    }

    /* 메세지 삭제 */
    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(
            @PathVariable String messageId
    ) {
        messageService.delete(parseStringToUuid(messageId));
        //TODO : 204번으로 리턴
        return ResponseEntity.ok().build();
    }

    /* 특정 채널의 메시지 목록을 조회 */
    @RequestMapping(path = "/{channelId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Message>> findAllByChannelId(
            @PathVariable String channelId
    ) {
        List<Message> MessageList = messageService.findAllByChannelId(parseStringToUuid(channelId));
        return ResponseEntity.ok().body(MessageList);
    }

    //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
    /* MultipartFile 타입 -> BinaryContentCreateRequest 타입으로 변경 */
    private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
        if (profile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
    /* String 타입 -> UUID 타입으로 변경 */
    private UUID parseStringToUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
        }
    }

}
