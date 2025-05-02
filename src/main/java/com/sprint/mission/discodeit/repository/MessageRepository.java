package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    public Message createMessage(MessageCreateRequestDto messageCreateRequestDto);

    public List<Message> getChannelMessages(UUID channelId);

    public boolean updateMessage(MessageUpdateRequestDto messageUpdateRequestDto);

    public void deleteMessage(MessageDeleteRequestDto messageDeleteRequestDto);
}
