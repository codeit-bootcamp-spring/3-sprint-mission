package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.Messages;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Messages messages = new Messages();
    private final JCFChannelService jcfChannelService;

    public JCFMessageService(JCFChannelService jcfChannelService) {
        this.jcfChannelService = jcfChannelService;
    }

    @Override
    public Message createMessage(String text, UUID channelID, UUID userID) {
        Message newMessage = new Message(text, channelID, userID);
        messages.add(newMessage.getId(), newMessage);
        jcfChannelService.addMessageToChannel(channelID, newMessage.getId());
        return newMessage;
    }

    @Override
    public Map<UUID, Message> readMessages() {
        return messages.readAll();
    }

    @Override
    public Message readMessage(UUID id) {
        return messages.get(id);
    }

    @Override
    public Message updateMessage(UUID id, String text) {
        return messages.update(id, text);
    }

    @Override
    public Message deleteMessage(UUID id) {
        return messages.remove(id);
    }
}
