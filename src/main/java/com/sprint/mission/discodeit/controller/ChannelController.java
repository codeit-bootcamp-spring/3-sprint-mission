package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.controller
 * fileName       : ChannelController
 * author         : doungukkim
 * date           : 2025. 5. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 5. 10.        doungukkim       최초 생성
 */
@Controller
@RequestMapping("api/channel/*")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @ResponseBody
    @RequestMapping(path = "/create-public", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody PublicChannelCreateRequest request) {
        return channelService.createChannel(request);
    }

    @ResponseBody
    @RequestMapping(path = "/create-private", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.createChannel(request);
    }

    @ResponseBody
    @RequestMapping(path = "/change-name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeName(@RequestBody ChannelUpdateRequest request) {
        return channelService.update(request);
    }

    @ResponseBody
    @RequestMapping(path = "/remove")
    public ResponseEntity<?> removeChannel(@RequestParam String channelId) {
        return channelService.deleteChannel(channelId);
    }

    @ResponseBody
    @RequestMapping(path = "/find-channels", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findChannels(@RequestBody ChannelFindByUserIdRequest request) {
        return channelService.findAllByUserId(request);
    }

}
