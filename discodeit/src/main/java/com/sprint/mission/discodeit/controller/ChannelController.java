package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
        private final ChannelService channelService;
        private final MessageRepository messageRepository;

        @RequestMapping(value = "/public",method = RequestMethod.POST)
        public ResponseEntity<PublicChannelDTO> createPublicChannel(@RequestBody CreatePublicChannelRequest createPublicChannelRequest) {
                Channel publicChannel = channelService.create(createPublicChannelRequest);
                Instant lastMessageAt = getLastMessageAt(publicChannel.getId());
                return ResponseEntity.ok(PublicChannelDTO.fromDomain(publicChannel, lastMessageAt));
        }

        @RequestMapping(value = "/private",method = RequestMethod.POST)
        public ResponseEntity<PrivateChannelDTO> createPrivateChannel(@RequestBody CreatePrivateChannelRequest createPrivateChannelRequest) {
                Channel privateChannel = channelService.create(createPrivateChannelRequest);
                Instant lastMessageAt = getLastMessageAt(privateChannel.getId());
                return ResponseEntity.ok(PrivateChannelDTO.fromDomain(privateChannel, lastMessageAt));
        }

        @RequestMapping(value = "/public",method = RequestMethod.GET)
        public ResponseEntity<PublicChannelDTO> findPublicChannel(@RequestParam("id") UUID channelId) {
                Channel publicChannel = channelService.find(channelId);
                Instant lastMessageAt = getLastMessageAt(publicChannel.getId());
                return ResponseEntity.ok(PublicChannelDTO.fromDomain(publicChannel, lastMessageAt));
        }

        @RequestMapping(value = "/private",method = RequestMethod.GET)
        public ResponseEntity<PrivateChannelDTO> findPrivateChannel(@RequestParam("id") UUID channelId) {
                Channel privateChannel = channelService.find(channelId);
                Instant lastMessageAt = getLastMessageAt(privateChannel.getId());
                return ResponseEntity.ok(PrivateChannelDTO.fromDomain(privateChannel, lastMessageAt));
        }

        @RequestMapping(value = "/{userId}/channel-list",method = RequestMethod.GET)
        public ResponseEntity<List<Object>> findAllChannelByUserId(@PathVariable("userId") UUID userId) {
                List<Channel> channelList = channelService.findAllByUserId(userId);

                List<Object> channelDTOs = channelList.stream()
                        .map(channel -> {
                                Instant lastMessageAt = getLastMessageAt(channel.getId());
                                return channel.getType().equals(ChannelType.PRIVATE)
                                        ? PrivateChannelDTO.fromDomain(channel, lastMessageAt)
                                        : PublicChannelDTO.fromDomain(channel, lastMessageAt);
                        })
                        .collect(Collectors.toList());

                return ResponseEntity.ok(channelDTOs);
        }

        @RequestMapping(value = "/public", method = RequestMethod.PATCH) // private는 수정이 불가능하여 public 채널만 일부 수정을 위해 RequestMethod.PATCH 사용
        public ResponseEntity<PublicChannelDTO> updateChannel(@RequestBody UpdateChannelRequest updateChannelRequest) {
                Channel channel = channelService.update(updateChannelRequest);
                Instant lastMessageAt = getLastMessageAt(channel.getId());
                return ResponseEntity.ok(PublicChannelDTO.fromDomain(channel, lastMessageAt));
        }

        @RequestMapping(method = RequestMethod.DELETE)
        public ResponseEntity<String> deleteChannel(@RequestParam("id") UUID channelId) {

                channelService.delete(channelId);
                return ResponseEntity.ok("채널 ID : " + channelId + "삭제 완료");
        }



        private Instant getLastMessageAt(UUID channelId) { // 가장 최근 메시지 생성 시간을 가져오는 메서드
                return messageRepository.findAllByChannelId(channelId)
                        .stream()
                        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                        .map(Message::getCreatedAt)
                        .limit(1)
                        .findFirst()
                        .orElse(Instant.MIN);
        }

}
