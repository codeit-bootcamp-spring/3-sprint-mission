package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import com.sprint.mission.discodeit.dto.message.JpaMessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : MessageMapper
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    public JpaMessageResponse toDto(Message message) {
        if(message == null) return null;

        List<JpaBinaryContentResponse> attachmentsDto = new ArrayList<>();
        List<BinaryContent> attachments = message.getAttachments();
        for(BinaryContent attachment : attachments) {
            attachmentsDto.add(binaryContentMapper.toDto(attachment));
        }

        return JpaMessageResponse.builder()
                .id(message.getId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .content(message.getContent())
                .channelId(message.getChannel().getId())
                .author(userMapper.toDto(message.getAuthor()))
                .attachments(attachmentsDto)
                .build();
    }
}
