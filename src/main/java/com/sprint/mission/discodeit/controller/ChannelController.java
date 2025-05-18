package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<Channel> createChannel(@RequestParam String name) {
        Channel created = channelService.create(name);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Channel>> getAllChannels() {
        return ResponseEntity.ok(channelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable UUID id) {
        Optional<Channel> channel = channelService.findById(id);
        return channel.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Channel> updateChannel(@PathVariable UUID id,
                                                 @RequestParam String name) {
        Optional<Channel> updated = channelService.update(id, name);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID id) {
        channelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}