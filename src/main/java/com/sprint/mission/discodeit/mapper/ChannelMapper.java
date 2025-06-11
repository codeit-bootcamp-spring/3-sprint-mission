package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        if (channel == null) {
            return null;
        }

        // 가장 최근 메시지 조회
        Instant lastMessageAt = messageRepository
            .findTop1ByChannelIdOrderByCreatedAtDesc(channel.getId())
            .map(Message::getCreatedAt)
            .orElse(Instant.MIN);

        // PRIVATE 채널인 경우에만 참가자 조회
        List<UserDto> participants = null;
        if (ChannelType.PRIVATE.equals(channel.getType())) {
            participants = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
        }

        return new ChannelDto(
            channel.getId(),
            channel.getType(),
            channel.getName(),
            channel.getDescription(),
            participants,
            lastMessageAt
        );
    }
}
