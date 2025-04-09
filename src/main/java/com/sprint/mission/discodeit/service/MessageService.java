package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;

public interface MessageService {
    public List<Message> inputMessage(User user, Channel channel, String newMessage);

    public void outputUserMessage();

    public void updateUserMessage(List<Message> messages, int number, String newMessage);

    public void deleteUserMessage(List<Message> messages, int number);
}
