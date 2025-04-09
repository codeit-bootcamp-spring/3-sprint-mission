package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;

public interface MessageService {
    public List<Message> inputMessage(String newMessage);

    public void outputUserMessage();

    public void updateUserMessage(int number, String newMessage);

    public void deleteUserMessage(int number);
}