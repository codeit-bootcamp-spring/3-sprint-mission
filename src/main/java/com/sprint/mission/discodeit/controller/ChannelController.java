package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channel")
@Controller
public class ChannelController {

    private final ChannelService channelService;

    // PUBLIC Channel Create( POST )
    @RequestMapping(
            path = "/public-channel"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequest request
    ) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(createdChannel);

    }


    // PRIVATE Channel Create( POST )
    @RequestMapping(
            path = "/private-channel"
            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        Channel createdChannel = channelService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(createdChannel);
    }



    // 공개 채널 수정( PUT )
    @RequestMapping(
            path = "/{channelId}"
            , method = RequestMethod.PUT
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Channel> updatePublicChannel(
            @PathVariable UUID channelId
            , @RequestBody ChannelUpdateRequest request
    ) {
        Channel updatedChannel = channelService.update(channelId, request);

        // 유효성 검사
        if (updatedChannel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }


    // 채널 삭제( DEL )
    @RequestMapping(
            path = "/{channelId}"
            , method = RequestMethod.DELETE
    )
    @ResponseBody
    public ResponseEntity<String> delete(
            @PathVariable UUID channelId
    ) {
        try {
            channelService.delete(channelId);
            return ResponseEntity.status(HttpStatus.OK).body("채널 삭제에 성공하였습니다");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 채널을 찾을 수 없습니다");
        }
    }


    // 특정 사용자가 조회 가능한 모든 채널 목록 조회( GET )
    @RequestMapping(
            path = "/{userId}/channels"
            , method = RequestMethod.GET
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<List<ChannelDTO>> findAllByUserId(
            @PathVariable UUID userId
    ) {
        List<ChannelDTO> userChannels = channelService.findAllByUserId(userId);

        // 리스트가 비었을 경우 응답 정보가 없다고 판단하여 204 발생
        if (userChannels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // 리스트 내부의 정보를 가져온 경우 정상 처리 응답
        return ResponseEntity.status(HttpStatus.OK).body(userChannels);
    }
}
