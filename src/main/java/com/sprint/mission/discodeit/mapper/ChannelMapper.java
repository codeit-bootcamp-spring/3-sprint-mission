package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            resolveParticipants(channel),
            resolveLastMessageAt(channel)
        );
    }

    private Instant resolveLastMessageAt(Channel channel) {
        return messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId())
            .map(Message::getCreatedAt)
            .orElse(Instant.MIN);
    }

    private List<UserDto> resolveParticipants(Channel channel) {
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            return readStatusRepository.findAllByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
        }
        return List.of();
    }
}