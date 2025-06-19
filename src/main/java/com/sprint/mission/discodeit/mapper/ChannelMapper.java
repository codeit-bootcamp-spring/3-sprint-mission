package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected ReadStatusRepository readStatusRepository;

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(resolveParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(resolveLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    protected List<UserDto> resolveParticipants(Channel channel) {
        try {
            return readStatusRepository.findAllByChannelIdWithUser(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 실패 시 빈 리스트 반환
            return List.of();
        }
    }

    protected Instant resolveLastMessageAt(Channel channel) {
        try {
            return messageRepository.findTop1ByChannelIdOrderByCreatedAtDesc(channel.getId())
                    .map(message -> message.getCreatedAt())
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
