package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/channel")
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(path = "/createPublic", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Channel> createPublicChannel(@RequestBody PublicChannelDTO publicChannelDTO) {
        Channel createdChannel = channelService.createPublicChannel(publicChannelDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @RequestMapping(path = "/createPrivate", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody PrivateChannelDTO privateChannelDTO) {
        Channel createdChannel = channelService.createPrivateChannel(privateChannelDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ChannelResponseDTO> findById(@RequestParam UUID channelId) {
        ChannelResponseDTO foundChannel = channelService.findById(channelId);

        return ResponseEntity.status(HttpStatus.OK).body(foundChannel);
    }

    @RequestMapping(path = "/findByNameContaining", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelResponseDTO>> findByNameContaining(@RequestParam String name) {
        List<ChannelResponseDTO> foundChannels = channelService.findByNameContaining(name);

        return ResponseEntity.status(HttpStatus.OK).body(foundChannels);
    }

    @RequestMapping(path = "/findAllByUser", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelResponseDTO>> findAllByUserId(@RequestParam UUID userId) {
        List<ChannelResponseDTO> foundChannels = channelService.findAllByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(foundChannels);
    }

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ChannelResponseDTO>> findAll() {
        List<ChannelResponseDTO> allChannels = channelService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(allChannels);
    }

    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ChannelResponseDTO> update(@RequestParam UUID channelId,
                                                     @RequestBody PublicChannelDTO publicChannelDTO) {
        ChannelResponseDTO updatedChannel = channelService.update(channelId, publicChannelDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteById(@RequestParam UUID channelId) {
        channelService.deleteById(channelId);

        return ResponseEntity.status(HttpStatus.OK).body("[Success]: 채널 삭제 성공!");
    }
}
