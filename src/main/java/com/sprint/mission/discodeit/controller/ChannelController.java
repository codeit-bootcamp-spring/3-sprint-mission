package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @RequestMapping(path = "/create/public")
    @ResponseBody
    public ResponseEntity<Channel> createPublicChannel(
            @RequestPart("publicChannelCreateRequest") PublicChannelRequest request
    ){
        Channel channel = channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    // 비공개 채널 생성
    @RequestMapping(path = "/create/private")
    @ResponseBody
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestPart("privateChannelCreateRequest") PrivateChannelRequest request
    ){
        Channel channel = channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    // 공개 채널 이름 수정
    @RequestMapping(path = "/update-name")
    @ResponseBody
    public ResponseEntity<String> updateName(@RequestBody ChannelUpdateNameRequest request) {
        boolean result = channelService.updateChannelName(request);
        return result ? ResponseEntity.ok("채널 이름 수정 완료")
                : ResponseEntity.badRequest().body("수정 실패: 비공개 채널이거나 존재하지 않음");
    }

    // 채널 삭제
    @RequestMapping(path = "/delete")
    @ResponseBody
    public ResponseEntity<String> deleteChannel(
            @RequestParam UUID channelId,
            @RequestParam UUID userId,
            @RequestParam String password
    ){
        boolean result = channelService.deleteChannel(channelId, userId, password);
        return result ? ResponseEntity.ok("채널 삭제 성공")
                : ResponseEntity.badRequest().body("채널 삭제 실패");
    }

    // 특정 사용자가 조회할 수 있는 채널
    @RequestMapping(path = "/find-all")
    @ResponseBody
    public ResponseEntity<List<ChannelFindResponse>> getChannelsByUser(
            @RequestParam UUID userId
    ){
        return ResponseEntity.ok(channelService.findAllChannel(userId));
    }

    // 채널 아이디로 조회
    @RequestMapping(path = "/find-one")
    @ResponseBody
    public ResponseEntity<ChannelFindResponse> findChannel(
            @RequestParam UUID channelId
    ){
        return ResponseEntity.ok(channelService.getChannelUsingId(channelId));
    }
}
