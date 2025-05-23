package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RequestMapping("/api") // 공통 경로 설정
@Controller
public class MessageController {

    private final MessageService messageService;

    // 메시지 전송
    @RequestMapping(
            path = "/channel/{channelId}/message", // 경로 변수 channelId 사용
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> sendMessage(
            @PathVariable("channelId") UUID pathChannelId, // 경로의 채널 ID
            @RequestBody MessageCreateRequest request
    ) {
        // 요청 DTO의 channelId와 경로의 channelId가 일치하는지 확인 (선택적 강화)
        if (!request.channelId().equals(pathChannelId)) {
            return ResponseEntity.badRequest().body("{\"error\": \"Channel ID in path does not match channel ID in request body.\"}");
        }
        try {
            Message createdMessage = messageService.createMessage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to send message: " + e.getMessage() + "\"}");
        }
    }

    // 메시지 수정
    @RequestMapping(
            path = "/update/message/{messageId}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> updateMessage(
            @PathVariable("messageId") UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        try {
            Message updatedMessage = messageService.updateMessage(messageId, request);
            return ResponseEntity.ok(updatedMessage);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SecurityException e) { // 권한 예외 처리 (예시)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to update message: " + e.getMessage() + "\"}");
        }
    }

    // 메시지 삭제
    @RequestMapping(
            path = "/delete/message/{messageId}",
            method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        try {
            messageService.deleteMessage(messageId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 본문 없이 404
        } catch (SecurityException e) { // 권한 예외 처리 (예시)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 특정 채널의 메시지 목록 조회
    @RequestMapping(
            path = "/channel/{channelId}/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> getChannelMessages(@PathVariable("channelId") UUID channelId) {
        try {
            List<Message> messages = messageService.findAllByChannelId(channelId);
            return ResponseEntity.ok(messages);
        } catch (NoSuchElementException e) { // 채널이 없을 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to retrieve messages: " + e.getMessage() + "\"}");
        }
    }
}