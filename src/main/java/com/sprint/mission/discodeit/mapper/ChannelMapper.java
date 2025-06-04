package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public ChannelMapper(MessageRepository messageRepository,
            ReadStatusRepository readStatusRepository,
            UserMapper userMapper) {
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
        this.userMapper = userMapper;
    }

    public ChannelDto toDto(Channel channel) {

        Message lastMessage = messageRepository.findTopByChannel_IdOrderByCreatedAtDesc(
                channel.getId()).orElse(null);

        Instant lastMessageAt = lastMessage != null ? lastMessage.getCreatedAt() : null;

        List<UserDto> users = null;

        if (ChannelType.PRIVATE.equals(channel.getType())) {
            users = readStatusRepository.findAllByChannel_Id(channel.getId())
                    .stream()
                    .map(rs -> userMapper.toDto(rs.getUser()))
                    .toList();

        }

        return new ChannelDto(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getType(),
                lastMessageAt,
                users
        );
    }
}
