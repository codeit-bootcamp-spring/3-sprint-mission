package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository
                .findByChannelId(
                        channel.getId(),
                        PageRequest.of(0, 1, Sort.by("createdAt").descending())
                )
                .stream()
                .findFirst()
                .map(BaseEntity::getCreatedAt)
                .orElse(null);

        List<UserDto> participants = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository
                    .findAllByChannelId(channel.getId())
                    .stream()
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