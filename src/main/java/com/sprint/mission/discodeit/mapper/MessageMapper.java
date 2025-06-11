package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        if (message == null) {
            return null;
        }

        UserDto author = userMapper.toDto(message.getAuthor());

        List<UUID> attachmentIds = Optional.ofNullable(message.getAttachments())
            .orElse(List.of())
            .stream()
            .map(BinaryContent::getId)
            .toList();

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(attachmentIds);
        List<BinaryContentDto> binaryContentDtos = binaryContents.stream()
            .map(binaryContentMapper::toDto)
            .toList();

        return new MessageDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            author,
            binaryContentDtos
        );
    }

}
