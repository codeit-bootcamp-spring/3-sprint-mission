package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelResponseDto toDto(Channel channel) {
        List<UserResponseDto> participants = getParticipants(channel);
        Instant lastMessageAt = getLastMessageAt(channel);

        return new ChannelResponseDto(channel.getId(), channel.getType(), channel.getName(),
                channel.getDescription(), participants, lastMessageAt);
    }

    private List<UserResponseDto> getParticipants(Channel channel) {
        List<UserResponseDto> participants = new ArrayList<>();

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(readStatus -> userMapper.toDto(readStatus.getUser()))
                    .forEach(participants::add);
        }

        return participants;
    }

    private Instant getLastMessageAt(Channel channel) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        return messageRepository.findPageByChannelId(channel.getId(), pageable)
                .stream()
                .map(Message::getCreatedAt)
                .findFirst()
                .orElse(Instant.MIN);
    }
}
