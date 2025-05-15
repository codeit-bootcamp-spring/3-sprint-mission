package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api/channel")
@Controller
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping("/createPublic")
    @ResponseBody
    public ResponseEntity<Channel> createPublic(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {

        Channel publicChannel = channelService.create(publicChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    @RequestMapping("/createPrivate")
    @ResponseBody
    public ResponseEntity<Channel> createPrivate(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {

        Channel privateChannel = channelService.create(privateChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }

    @RequestMapping("/updatePublic")
    @ResponseBody
    public ResponseEntity<Channel> updatePublic(
            @RequestParam UUID channelId,
            @RequestBody PublicChannelUpdateRequest request
    ) {

        Channel updatedPublic = channelService.update(channelId, request);

        return ResponseEntity.status(HttpStatus.OK).body(updatedPublic);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> delete(
            @RequestParam UUID channelId
    ) {
        channelService.delete(channelId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("유저 정보가 삭제되었습니다.");
    }

    @RequestMapping("/findAllByUserId")
    @ResponseBody
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<ChannelDto> channelList = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(channelList);
    }


}
