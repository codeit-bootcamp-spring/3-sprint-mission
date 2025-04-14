package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final Map<UUID, Message> messages;
    private final ChannelService channelService;


    public FileMessageService(ChannelService channelService) {
        this.channelService = channelService;
        this.messages = new HashMap<>();
    }

    @Override
    public Message createMessage(String text, UUID channelID, UUID userID) {
        Message newMessage = new Message(text, channelID, userID);
        messages.put(newMessage.getId(), newMessage);
        channelService.addMessageToChannel(channelID, newMessage.getId());
        return newMessage;
    }

    @Override
    public Map<UUID, Message> readMessages() {
        return messages;
    }

    @Override
    public Message readMessage(UUID id) {
        return messages.get(id);
    }

    @Override
    public Message updateMessage(UUID id, String text) {
        Message message = messages.get(id);
        message.updateText(text);
        return message;
    }

    @Override
    public Message deleteMessage(UUID id) {
        return messages.remove(id);
    }




}
