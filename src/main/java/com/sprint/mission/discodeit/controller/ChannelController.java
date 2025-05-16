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
 * packageName    : com.sprint.mission.discodeit.controller fileName       : ChannelController
 * author         : doungukkim date           : 2025. 5. 10. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 5. 10.        doungukkim최초 생성
 */
@RestController
@RequestMapping("api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // request, endpoint, (param, body, variable) :
    // service, repository : OK
    // response : OK
    // fail response : OK
    @RequestMapping(path = "/public", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody PublicChannelCreateRequest request) {
        System.out.println("ChannelController.create-public");
        return channelService.createChannel(request);
    }

    @ResponseBody
    @RequestMapping(path = "/private", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody PrivateChannelCreateRequest request) {
        System.out.println("ChannelController.create-private");
        return channelService.createChannel(request);
    }

    @ResponseBody
    @RequestMapping(path = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeChannel(@PathVariable UUID channelId) {
        System.out.println("ChannelController.removeChannel");
        return channelService.deleteChannel(channelId);
    }

    @ResponseBody
    @RequestMapping(path = "/{channelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeName(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request) {
        System.out.println("ChannelController.changeName");
        return channelService.update(channelId, request);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?> findChannels(@PathVariable UUID userId) {
        System.out.println("ChannelController.findChannels");
        return channelService.findAllByUserId(userId);
    }
}
