package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/channels")
public class ChannelController {
  private final ChannelService channelService;

  @RequestMapping("/createPublic")
  public ResponseEntity<Channel> create(@RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @RequestMapping("/createPrivate")
  public ResponseEntity<Channel> create(@RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
  }

  @RequestMapping("/update")
  public ResponseEntity<Channel> update(@RequestParam("channelId") UUID channelId,
                                        @RequestBody PublicChannelUpdateRequest request) {
    Channel updatedChannel = channelService.update(channelId,request);
    return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
  }

  @RequestMapping("/delete")
  public ResponseEntity<Void> delete(@RequestParam("channelId") UUID channelId) {
    channelService.deleteChannel(channelId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @RequestMapping("/findAll")
  public ResponseEntity<List<ChannelDto>> findAll(@RequestParam("userId") UUID userId) {
   List<ChannelDto> channels = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(channels);
  }

  @RequestMapping("/addMember")
  public ResponseEntity<Void> addMember(@RequestParam("channelId") UUID channelId,
                                        @RequestParam("userId") UUID userId){
    channelService.addMember(channelId,userId);
    return ResponseEntity.ok().build();
  }
}
