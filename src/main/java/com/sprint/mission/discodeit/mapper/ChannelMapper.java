package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.serviceDto.ChannelDto;
import com.sprint.mission.discodeit.dto.serviceDto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        UUID id = channel.getId();
        ChannelType type = channel.getType();
        String name = channel.getName();
        String description = channel.getDescription();
        List<UserDto> participants = readStatusRepository.findAllByChannelId(id).stream()
            .map(ReadStatus::getUser)
            .map(userMapper::toDto)
            .collect(Collectors.toList());
        Instant lastMessageAt = messageRepository.findTop1ByChannelIdOrderByCreatedAtDesc(id)
            .stream()
            .map(Message::getCreatedAt)
            .max(Comparator.naturalOrder())
            .orElse(null);

        return new ChannelDto(id, type, name, description, participants, lastMessageAt);
    }
}
