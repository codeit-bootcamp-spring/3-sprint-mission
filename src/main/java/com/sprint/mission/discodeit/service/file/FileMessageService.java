package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileMessageService implements MessageService {

    private List<Message> messages;
    private final static String FILEPATH = "messages.ser";

    public FileMessageService() {
        messages = loadMessages();
        if (messages == null) {
            messages = new ArrayList<>();
        } else {
            int max = 0;
            for (Message message : messages) {
                if (message.getNumber() > max) {
                    max = message.getNumber();
                }
            }
            Message.setCounter(max + 1);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadMessages() {
        File file = new File(FILEPATH);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveMessages() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILEPATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> inputMessage(String newMessage) {
        Message newMsg = new Message(newMessage);
        messages.add(newMsg);
        saveMessages();
        return messages;
    }

    @Override
    public void outputUserMessage() {
        if (messages.isEmpty()) {
            System.out.println("메시지가 비었습니다.");
        } else {
            for (Message message1 : messages) {
                System.out.println(message1);
            }
        }
    }

    @Override
    public void updateUserMessage(int number, String newMessage) {
        messages.stream()
                .filter(m -> m.getNumber() == number)
                .findFirst()
                .ifPresent(message1 -> message1.updateMessage(newMessage));
        saveMessages();
    }

    @Override
    public void deleteUserMessage(int number) {
        messages.stream()
                .filter(message1 -> message1.getNumber() == number)
                .findFirst()
                .ifPresent(message1 -> messages.remove(message1));
        saveMessages();
    }
}