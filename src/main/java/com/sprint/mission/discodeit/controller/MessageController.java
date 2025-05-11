package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @RequestMapping(path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<Message> create(@RequestPart("messageRequest") MessageRequestDTO messageRequestDTO,
                                          @RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
        List<BinaryContentDTO> binaryContentDTOS = resolveFileRequest(attachedFiles);

        Message createdMessage = messageService.create(messageRequestDTO, binaryContentDTOS);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> findById(@RequestParam UUID messageId) {
        MessageResponseDTO foundMessage = messageService.findById(messageId);

        return ResponseEntity.status(HttpStatus.OK).body(foundMessage);
    }

    @RequestMapping(path = "/findAllByChannel", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<MessageResponseDTO>> findAllByChannelId(@RequestParam UUID channelId) {
        List<MessageResponseDTO> foundMessages = messageService.findAllByChannelId(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<MessageResponseDTO>> findAll() {
        List<MessageResponseDTO> foundMessages = messageService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
    }

    @RequestMapping(path = "/findAllByUser", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<MessageResponseDTO>> findAllByUserId(@RequestParam UUID userId) {
        List<MessageResponseDTO> foundMessages = messageService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
    }

    @RequestMapping(path = "/findAllByWord", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<MessageResponseDTO>> findAllByContainingWord(@RequestParam String word) {
        List<MessageResponseDTO> foundMessages = messageService.findAllByContainingWord(word);

        return ResponseEntity.status(HttpStatus.OK).body(foundMessages);
    }

    @RequestMapping(path = "/updateAttatchFiles",
            method = RequestMethod.PUT,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> updateBinaryContent(@RequestParam UUID messageId,
                                                                  @RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
        List<BinaryContentDTO> binaryContentDTOS = resolveFileRequest(attachedFiles);

        MessageResponseDTO updatedMessage = messageService.updateBinaryContent(messageId, binaryContentDTOS);

        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    @RequestMapping(path = "/updateContent", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<MessageResponseDTO> updateContent(@RequestParam UUID messageId, String content) {
        MessageResponseDTO updatedMessage = messageService.updateContent(messageId, content);

        return ResponseEntity.status(HttpStatus.OK).body(updatedMessage);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteById(@RequestParam UUID messageId) {
        messageService.deleteById(messageId);

        return ResponseEntity.status(HttpStatus.OK).body("[Success]: 메시지 삭제 성공!");
    }

    private List<BinaryContentDTO> resolveFileRequest(List<MultipartFile> attachedFiles) {
        if (attachedFiles == null || attachedFiles.isEmpty()) {
            return Collections.emptyList();
        }

        List<BinaryContentDTO> binaryContentList = new ArrayList<>();
        for (MultipartFile file : attachedFiles) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                binaryContentList.add(new BinaryContentDTO(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                ));
            } catch (IOException e) {
                throw new UncheckedIOException("파일 변환 중 오류 발생", e);
            }
        }

        return binaryContentList;
    }
}
