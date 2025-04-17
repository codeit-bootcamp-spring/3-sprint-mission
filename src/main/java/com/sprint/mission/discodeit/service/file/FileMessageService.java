package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileMessageService implements MessageService {
    private final FileDataStore<Message> store;
    private final Map<UUID, Message> data;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.store = new FileDataStore<>("data/messages.ser");
        this.data = store.load();
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        channelService.getChannel(channelId);
        userService.getUser(userId);

        Message message = new Message(userId, channelId, content);
        data.put(message.getId(), message);
        store.save(data);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        Message message = data.get(id);
        if (message == null) throw new NoSuchElementException("Message not found: " + id);
        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateMessage(Message message, String newContent) {
        message.updateContent(newContent);
        store.save(data);
        return message;
    }

    @Override
    public Message deleteMessage(Message message) {
        Message removed = data.remove(message.getId());
        store.save(data);
        return removed;
    }
}
