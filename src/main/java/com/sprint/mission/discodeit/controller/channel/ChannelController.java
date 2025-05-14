package com.sprint.mission.discodeit.controller.channel;

import com.sprint.mission.discodeit.entity.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.entity.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.entity.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.dto.channel.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @RequestMapping(path = "/public", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ChannelResponseDto> createPublicChannel(@RequestBody ChannelCreateDto channelCreateDto) {
        ChannelResponseDto channel = channelService.createChannel(channelCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(path = "/private", method = RequestMethod.POST)
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(@RequestBody ChannelCreatePrivateDto channelCreatePrivateDto) {
        ChannelResponseDto channel = channelService.createChannel(channelCreatePrivateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(channel);
    }

    @RequestMapping(path = "/public", method = RequestMethod.PUT)
    public ResponseEntity<?> modifyPublicChannel(@RequestBody ChannelUpdateRequestDto channelUpdateRequestDto) {
        boolean result = channelService.modifyChannel(channelUpdateRequestDto);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteChannel(@RequestParam UUID channelId, @RequestParam UUID userId) {
        boolean result = channelService.deleteChannel(channelId, userId);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }

    @RequestMapping("/users/{userId}")
    public ResponseEntity<List<ChannelResponseDto>> getChannelsByUserId(@PathVariable UUID userId) {
        List<ChannelResponseDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok().body(channels);
    }

    @RequestMapping("/join")
    public ResponseEntity<?> joinChannel(@RequestParam UUID channelId, @RequestParam UUID userId) {
        boolean result = channelService.joinChannel(channelId, userId);

        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fail");
        }

        return ResponseEntity.ok().body("Success");
    }
}
