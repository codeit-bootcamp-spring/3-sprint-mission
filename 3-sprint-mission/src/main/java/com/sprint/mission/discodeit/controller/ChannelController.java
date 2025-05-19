package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDTO;
import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/channel")
@ResponseBody
@Controller
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채팅방 개설
    @RequestMapping(
            value = "/create-public-channel"
//            , method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequest publicChannelCreateDTO
    ) {

        Channel createdChannel = channelService.create(publicChannelCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 비공개 채팅방 개설
    @RequestMapping(
            value = "/create-private-channel"
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateDTO
    ) {

        Channel createdChannel = channelService.create(privateChannelCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    // 채팅방 전체 조회
    @RequestMapping(
            value = "/findAll",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ChannelDTO>> findAll() {
        List<ChannelDTO> channels = channelService.findAll();
        return ResponseEntity.ok(channels);
    }

    // 공개 채널 정보 수정
    @RequestMapping(
            value = "/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Channel> update(
            @RequestParam UUID channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelUpdateDTO
    ) {
        Channel channel = channelService.update(channelId, publicChannelUpdateDTO);
        return ResponseEntity.ok(channel);
    }

    // 채팅방 삭제
    @RequestMapping(
            value = "/delete"
            , method = RequestMethod.DELETE
//            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> delete(
            @RequestParam UUID channelId
    ) {
        ChannelDTO channel = channelService.find(channelId);
        String channelName = channel.channelName();

        channelService.delete(channelId);

        return ResponseEntity.ok(channelName + "채팅방을 삭제했습니다.");
    }

    // 특정 유저가 볼 수 있는 채널 목록 조회
    @RequestMapping(
            value = "/findAll-by-user"
            , method = RequestMethod.GET
//            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ChannelDTO>> findByUserId(
            @RequestParam UUID userId
    ) {
        List<ChannelDTO> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }

}
