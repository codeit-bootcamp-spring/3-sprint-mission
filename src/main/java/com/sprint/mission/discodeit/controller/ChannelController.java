package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channel")
@Controller
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(path = "/createPublic"
            , method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
            ) {
        Channel channel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    // 비공개 채널 생성
    @RequestMapping(path = "/createPrivate"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
    ) {
        Channel channel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    // 공개 채널 정보 수정
    @RequestMapping(path = "/update"
            , method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Channel> update(
            @RequestParam("channelId") UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest
            ) {
        Channel channel = channelService.update(channelId, publicChannelUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    // 채널 삭제
    @RequestMapping(path = "/delete"
            , method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(
            @RequestParam("channelId") UUID channelId
    ) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 사용자가 볼 수 있는 모든 채널 목록 조회
    @RequestMapping(path = "/findAll"
            , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelDto>> findAllByUser(
            @RequestParam("userId") UUID userId
    ) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }

}
