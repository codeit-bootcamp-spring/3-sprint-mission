package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Controller
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(path = "/public", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createPublicChannel(
            @RequestBody PublicChannelCreateRequest request) {
        try {
            Channel createdChannel = channelService.createChannel(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create public channel: " + e.getMessage());
        }
    }

    // 비공개 채널 생성
    @RequestMapping(path = "/private", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request) {
        try {
            Channel createdChannel = channelService.createChannel(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create private channel: " + e.getMessage());
        }
    }

    // 유저의 채널 목록 조회
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getChannelsByUserId(@RequestParam("userId") UUID userId) {
        try {
            List<ChannelDto> channels = channelService.getChannelsByUserId(userId);
            return ResponseEntity.ok(channels);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with id " + userId + " not found or has no channels");
        }
    }

    // 공개 채널 정보 수정
    @RequestMapping(path = "/{channelId}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateChannel(
            @PathVariable("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest request) {
        try {
            Channel updatedChannel = channelService.updateChannel(request);
            return ResponseEntity.ok(updatedChannel);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Channel with id " + channelId + " not found");
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 채널 삭제
    @RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteChannel(
            @PathVariable("channelId") UUID channelId) {
        try {
            channelService.deleteChannel(channelId);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Channel with id " + channelId + " not found");
        }
    }
}