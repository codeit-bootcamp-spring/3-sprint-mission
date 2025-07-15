package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ChannelController implements ChannelApi {

    private final ChannelService channelService;

    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> create(@Valid @RequestBody PublicChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createChannel(request));
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponse> create(@Valid @RequestBody PrivateChannelCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createChannel(request));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<?> removeChannel(@PathVariable @NotNull UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> update(
            @PathVariable UUID channelId,
            @Valid @RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.ok(channelService.update(channelId, request));
    }

    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findChannels(@RequestParam UUID userId) {
        return ResponseEntity.ok(channelService.findAllByUserId(userId));
    }
}
