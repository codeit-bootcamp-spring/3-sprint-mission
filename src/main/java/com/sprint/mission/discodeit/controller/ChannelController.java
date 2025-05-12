package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

  private final ChannelService channelService;

  @RequestMapping(value = "/public", method = RequestMethod.POST)
  public ResponseEntity<ChannelResponse> create(
      @RequestBody PublicChannelCreateRequest request) {
    ChannelResponse response = channelService.create(request);
    return ResponseEntity.created(URI.create("/api/channel/public/" + response.id()))
        .body(response);
  }

  @RequestMapping(value = "/private", method = RequestMethod.POST)
  public ResponseEntity<ChannelResponse> create(
      @RequestBody PrivateChannelCreateRequest request) {
    ChannelResponse response = channelService.create(request);
    return ResponseEntity.created(URI.create("/api/channel/private/" + response.id()))
        .body(response);
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  public ResponseEntity<List<ChannelResponse>> findAllByUser(@PathVariable UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }

  @RequestMapping(value = "/public", method = RequestMethod.PUT)
  public ResponseEntity<ChannelResponse> update(
      @RequestBody PublicChannelUpdateRequest request) {
    Optional<ChannelResponse> updated = channelService.update(request);
    return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
  public ResponseEntity<ChannelResponse> delete(@PathVariable UUID channelId) {
    Optional<ChannelResponse> deleted = channelService.delete(channelId);
    return deleted.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
