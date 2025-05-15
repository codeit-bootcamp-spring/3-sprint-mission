package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.FindChannelRespond;
import com.sprint.mission.discodeit.dto.UpdateChannelRequest;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  //publicChannel 생성
  @PostMapping(path = "/channels/public")
  public ResponseEntity<Channel> craetePublicChannel(
      @RequestBody CreatePublicChannelRequest request) {
    Channel channel = channelService.create(
        new CreatePublicChannelRequest(request.channelName(), request.description()));
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  //privateChannel 생성
  @PostMapping(path = "/channels/private")
  public ResponseEntity<Channel> craetePrivateChannel(
      @RequestBody CreatePrivateChannelRequest request) {
    Channel channel = channelService.create(new CreatePrivateChannelRequest(request.users()));
    return ResponseEntity.status(HttpStatus.CREATED).body(channel);
  }

  //publicChannel 수정
  @PutMapping(path = "/channels/public")
  public ResponseEntity<String> updatePublicChannel(@RequestBody UpdateChannelRequest request) {
    channelService.update(request);
    return ResponseEntity.status(HttpStatus.OK).body("수정 완료!");
  }

  //Channel 삭제
  @DeleteMapping("/channels")
  public ResponseEntity<String> deleteChannel(@RequestParam UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
  }

  //특정 사용자의 Channel 조회
  @GetMapping("/channels")
  @ResponseBody
  public ResponseEntity<List<FindChannelRespond>> searchByUserId(@RequestParam UUID userId) {
    List<FindChannelRespond> allByUserId = channelService.findAllByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(allByUserId);
  }


}
