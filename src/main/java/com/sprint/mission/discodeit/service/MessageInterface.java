package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;

public interface MessageInterface {
    public List<Message> inputMessage(String newMessage);

    public void outputUserMessage();

    public void updateUserMessage(List<Message> messages, int number, String newMessage);

    public void deleteUserMessage(List<Message> messages, int number);
}
