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
        
        // 프록시 객체 안전 접근
        List<UserDto> participants = null;
        try {
            participants = readStatusRepository.findAllByChannelIdWithUser(id).stream()
                .map(readStatus -> {
                    try {
                        return readStatus.getUser();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(user -> user != null)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            participants = List.of();
        }
        
        Instant lastMessageAt = null;
        try {
            lastMessageAt = messageRepository.findTop1ByChannelIdOrderByCreatedAtDesc(id)
                .stream()
                .map(message -> {
                    try {
                        return message.getCreatedAt();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(createdAt -> createdAt != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 null 처리
        }

        return new ChannelDto(id, type, name, description, participants, lastMessageAt);
    }
}
