package com.sprint.mission.discodeit.controller.message;

import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryOwnerType;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(
            path = "/create",
            method = RequestMethod.POST
    )
    public ResponseEntity<MessageResponseDto> create(
            @RequestPart("messageCreateRequestDto") MessageCreateRequestDto messageCreateRequestDto,
            @RequestPart(value = "messageFiles", required = false) List<MultipartFile> messageFiles
    ) {
        List<byte[]> messageFileBytes = new ArrayList<>();
        BinaryContentCreateRequestDto binaryContentCreateRequestDto = null;

        if (messageFiles != null && !messageFiles.isEmpty()) {
            for (MultipartFile file : messageFiles) {
                try {
                    messageFileBytes.add(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            binaryContentCreateRequestDto = new BinaryContentCreateRequestDto(
                    BinaryContentType.MESSAGE_ATTACHMENT,
                    BinaryOwnerType.MESSAGE,
                    messageFiles.get(0).getContentType(),
                    messageFileBytes
            );
        }

        messageCreateRequestDto.setMessageFile(messageFileBytes);
        MessageResponseDto message = messageService.createMessage(messageCreateRequestDto, binaryContentCreateRequestDto);
        return ResponseEntity.ok().body(message);
    }


    @RequestMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody MessageUpdateRequestDto messageUpdateRequestDto) {
        boolean result = messageService.updateMessage(messageUpdateRequestDto);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }

    @RequestMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MessageDeleteRequestDto messageDeleteRequestDto) {
        boolean result = messageService.deleteMessage(messageDeleteRequestDto);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }

    @RequestMapping("/messages/{channelId}")
    public ResponseEntity<List<MessageResponseDto>> getChannelMessages(@PathVariable UUID channelId) {
        List<MessageResponseDto> messages = messageService.getChannelMessages(channelId);
        return ResponseEntity.ok().body(messages);
    }
}
