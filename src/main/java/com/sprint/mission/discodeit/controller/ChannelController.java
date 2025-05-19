package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class ChannelController {

  private final ChannelService channelService;


  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ResponseEntity<ChannelResponse> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request) {
    return ResponseEntity.ok(channelService.createPublicChannel(request));
  }

  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ResponseEntity<ChannelResponse> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request) {
    return ResponseEntity.ok(channelService.createPrivateChannel(request));
  }

  @RequestMapping(value = "/{channelId}", method = RequestMethod.PUT)
  public ResponseEntity<ChannelResponse> update(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<ChannelResponse> delete(@PathVariable UUID channelId) {
    return ResponseEntity.ok(channelService.delete(channelId));
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public ResponseEntity<List<ChannelDto>> findAllByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }


  @RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
  public ResponseEntity<ChannelDto> find(@PathVariable UUID channelId) {
    return ResponseEntity.ok(channelService.find(channelId));
  }
}
