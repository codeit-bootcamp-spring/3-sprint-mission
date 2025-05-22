package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/api/channel")
@Controller
public class ChannelController {

    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(
        path = "/public",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        Channel createdChannel = channelService.createChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 비공개 채널 생성
    @RequestMapping(
        path = "/private",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> createPrivateChannel(
        @RequestBody PrivateChannelCreateRequest request
    ){
        Channel createdChannel = channelService.createChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 공개 채널 정보 수정
    @RequestMapping(
        path = "/public/{channelId}",
        method = RequestMethod.PUT,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> updatePublicChannel(
        @PathVariable("channelId") UUID channelId,
        @RequestBody PublicChannelUpdateRequest request
    ){
        Channel updatedChannel = channelService.updateChannel(request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }

    // 채널 삭제 
    @RequestMapping(
        path = "/delete/{channelId}",
        method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<Void> deleteChannel(
        @PathVariable("channelId") UUID channelId
    ){
        channelService.deleteChannel(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록 조회 
    @RequestMapping(
        path = "/user/{userId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ChannelDto>> getChannelsByUserId(
        @PathVariable("userId") UUID userId
    ){
        List<ChannelDto> channels = channelService.getChannelsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
    
}