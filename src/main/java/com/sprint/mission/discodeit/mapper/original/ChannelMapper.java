package com.sprint.mission.discodeit.mapper.original;

import com.sprint.mission.discodeit.dto.channel.response.JpaChannelResponse;
import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.advanced.AdvancedUserMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
    private final AdvancedUserMapper userMapper;

    public JpaChannelResponse toDto(Channel channel) {
        if(channel == null) return null;

        Message message = messageRepository.findTopByChannelIdOrderByCreatedAtDesc((channel.getId()));
        Instant lastMessageAt = null;
        if (message != null) {
            lastMessageAt = message.getCreatedAt();
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelWithUser(channel);

        Set<User> users = readStatuses.stream()
                .map(ReadStatus::getUser)
                .collect(Collectors.toSet());

        List<JpaUserResponse> participants = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

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
