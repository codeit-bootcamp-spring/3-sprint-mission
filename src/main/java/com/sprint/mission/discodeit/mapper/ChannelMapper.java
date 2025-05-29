package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;


    public ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);

        List<UserDto> participants = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .toList();
        }

        return ChannelDto.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .lastMessageAt(lastMessageAt)
                .participants(participants)
                .build();
    }
}