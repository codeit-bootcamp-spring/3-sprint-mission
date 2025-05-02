package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDto);

    public List<MessageResponseDto> getChannelMessages(UUID channelId);

    public boolean updateMessage(MessageUpdateRequestDto messageUpdateRequestDto);

    public void deleteMessage(MessageDeleteRequestDto messageDeleteRequestDto);

}
