package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final JCFChannelService jcfChannelService;

    public JCFMessageService(JCFChannelService jcfChannelService) {
        this.jcfChannelService = jcfChannelService;
        this.data = new HashMap<>();
    }

    @Override
    public Message createMessage(String text, UUID channelID, UUID userID) {
        Message newMessage = new Message(text, channelID, userID);
        data.put(newMessage.getId(), newMessage);
        jcfChannelService.addMessageToChannel(channelID, newMessage.getId());
        return newMessage;
    }

    @Override
    public Map<UUID, Message> readMessages() {
        return data;
    }

    @Override
    public Message readMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public Message updateMessage(UUID id, String text) {
        Message message = data.get(id);
        message.updateText(text);
        return message;
    }

    @Override
    public Message deleteMessage(UUID id) {
        return data.remove(id);
    }
}
