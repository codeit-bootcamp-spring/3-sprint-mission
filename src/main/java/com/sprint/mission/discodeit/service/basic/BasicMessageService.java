package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepo;

    public BasicMessageService(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    @Override
    public List<Message> inputMessage(String newMessage) {
        Message message = new Message(newMessage);
        messageRepo.saveMessage(message);
        return messageRepo.findAllMessage();
    }

    @Override
    public void outputUserMessage() {
        for (Message message : messageRepo.findAllMessage()) {
            System.out.println(message);
        }
    }

    @Override
    public void updateUserMessage(int number, String newMessage) {
        Message message = messageRepo.findMessage(number);
        if (message != null) {
            message.setMessage(newMessage);
            messageRepo.updateMessage(message);
        }
    }

    @Override
    public void deleteUserMessage(int number) {
        messageRepo.deleteMessage(number);
    }
}