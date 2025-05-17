package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageRepository implements MessageRepository {
    private static final Map<UUID, List<Message>> channelMsgBoard = new ConcurrentHashMap<>();

    @Override
    public List<Message> getMessages(UUID channelId) {
        List<Message> messages = channelMsgBoard.get(channelId);
        if (messages == null) {
            messages = new ArrayList<Message>();
            channelMsgBoard.put(channelId, messages);
        }
        return messages;
    }

    @Override
    public void saveMessages(UUID channelId, List<Message> messages) {
        channelMsgBoard.put(channelId, messages);
    }
}
