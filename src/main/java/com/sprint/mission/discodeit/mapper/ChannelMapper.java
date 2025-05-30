package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.Dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.Dto.channel.JpaChannelResponse;
import com.sprint.mission.discodeit.Dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : ChannelMapper
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final JpaMessageRepository messageRepository;
    private final JpaReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    public JpaChannelResponse toDto(Channel channel) {
        if(channel == null) return null;

        Message message = messageRepository.findTopByChannelIdOrderByCreatedAtDesc((channel.getId()));
        Instant lastMessageAt = null;
        if (message != null) {
            lastMessageAt = message.getCreatedAt();
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel(channel);
        List<JpaUserResponse> participants = new ArrayList<>();
        if (!readStatuses.isEmpty()) {
            Set<User> users = readStatuses.stream().map(rs -> rs.getUser()).collect(Collectors.toSet());
            users.stream().map(user -> userMapper.toDto(user))
            .forEach(participants::add);
        }

        return JpaChannelResponse.builder()
                .id(channel.getId())
                .type(channel.getType())
                .name(channel.getName())
                .description(channel.getDescription())
                .participants(participants)
                .lastMessageAt(lastMessageAt)
                .build();
    }
}
