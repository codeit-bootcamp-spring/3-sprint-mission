package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelService channelService;

    /* 공개 채널 생성 */
    @RequestMapping(path = "/public", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Channel> createPublicChannel(
            @RequestBody PublicChannelCreateRequest publicChannelCreateRequest
    ) {
        Channel createdChannel = channelService.create(publicChannelCreateRequest);
        return ResponseEntity.created(URI.create(createdChannel.getId().toString())).body(createdChannel);
    }

    /* 비공개 채널 생성 */
    @RequestMapping(path = "/private", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Channel> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest
    ) {
        Channel createdChannel = channelService.create(privateChannelCreateRequest);
        return ResponseEntity.created(URI.create(createdChannel.getId().toString())).body(createdChannel);
    }

    /* 공개 채널 수정 */
    @RequestMapping(path = "/public/{channelId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Channel> update(
            @PathVariable String channelId,
            @RequestBody PublicChannelUpdateRequest publicChannelCreateRequest

    ) {
        Channel updatedChannel = channelService.update(parseStringToUuid(channelId), publicChannelCreateRequest);
        return ResponseEntity.ok().body(updatedChannel);
    }

    /* 채널 삭제 */
    @RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(
            @PathVariable String channelId
    ) {
        channelService.delete(parseStringToUuid(channelId));
        //TODO : 204번으로 리턴
        return ResponseEntity.ok().build();
    }

    /* 특정 사용자가 볼 수 있는 모든 채널 목록 조회 */
    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
            @PathVariable String userId
    ) {
        List<ChannelDto> ChannelDtoList = channelService.findAllByUserId(parseStringToUuid(userId));
        return ResponseEntity.ok().body(ChannelDtoList);
    }

    //FIXME : util로 빼기 (모든 컨트롤러에서 중복됨)
    /* String 타입 -> UUID 타입으로 변경 */
    private UUID parseStringToUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("올바른 파라미터 형식이 아닙니다.");
        }
    }

}
