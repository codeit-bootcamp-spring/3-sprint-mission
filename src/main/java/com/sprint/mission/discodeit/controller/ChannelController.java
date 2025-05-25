package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 *  POST   /api/channels/public
 *  POST   /api/channels/private
 *  PATCH  /api/channels/{channelId}
 *  DELETE /api/channels/{channelId}
 *  GET    /api/channels?userId={userId}
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelService channelService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/public")
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    Channel created = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);   // 201
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/private")
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    Channel created = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);   // 201
  }


  @PatchMapping("/{channelId}")
  public ResponseEntity<Channel> update(@PathVariable UUID channelId,
                                        @RequestBody PublicChannelUpdateRequest request) {
    return ResponseEntity.ok(channelService.update(channelId, request));
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();                        // 204
  }

  @GetMapping
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam UUID userId) {
    return ResponseEntity.ok(channelService.findAllByUserId(userId));
  }
}
