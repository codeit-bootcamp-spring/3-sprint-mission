package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageInterface;
import java.util.ArrayList;
import java.util.List;

public class JCFMessageService implements MessageInterface {
    private final List<Message> messages = new ArrayList<>();

    public List<Message> inputMessage(String newMessage) {
        Message newMsg = new Message(newMessage);
        messages.add(newMsg);
        return messages;
    }

    public void outputUserMessage() {
        if (messages.isEmpty()) {
            System.out.println("메시지가 비었습니다.");
        } else {
            for (Message message1 : messages) {
                System.out.println(message1);
            }
        }

    }

    public void updateUserMessage(List<Message> messages, int number, String newMessage) {
        messages.stream()
                .filter(message1 -> message1.getNumber() == number)
                .findFirst()
                .ifPresent(message1 -> message1.updateMessage(newMessage));
    }

    public void deleteUserMessage(List<Message> messages, int number) {
        messages.stream()
                .filter(message1 -> message1.getNumber() == number)
                .findFirst()
                .ifPresent(message1 -> messages.remove(message1));
    }
}
