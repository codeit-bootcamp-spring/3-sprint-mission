package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.Channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.Channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(method = RequestMethod.POST, path = "/public")
    @ResponseBody
    public ResponseEntity<Channel> createPublic(@RequestBody PublicChannelCreateRequest request) {
        return ResponseEntity.ok(channelService.create(request));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/private")
    @ResponseBody
    public ResponseEntity<Channel> createPrivate(@RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.ok(channelService.create(request));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Channel> update(@RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.ok(channelService.update(request));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        channelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<ChannelResponse>> findAllByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}