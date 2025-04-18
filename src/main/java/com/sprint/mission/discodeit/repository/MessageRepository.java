package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;

public interface MessageRepository {

    void saveMessage(Message message);

    void updateMessage(Message message);

    void deleteMessage(int messageNumber);

    Message findMessage(int messageNumber);

    List<Message> findAllMessage();
}