package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileMessageRepository implements MessageRepository {

    private final String FILEPATH = "messages.ser";
    private List<Message> messages;

    public FileMessageRepository() {
        messages = loadMessages();
        if (messages == null) {
            messages = new ArrayList<>();
        } else {
            int max = messages.stream().mapToInt(Message::getNumber).max().orElse(0);
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
    public void saveMessage(Message message) {
        messages.add(message);
        saveMessages();
    }

    @Override
    public void updateMessage(Message message) {
        deleteMessage(message.getNumber());
        messages.add(message);
        saveMessages();
    }

    @Override
    public void deleteMessage(int messageNumber) {
        messages.removeIf(m -> m.getNumber() == messageNumber);
        saveMessages();
    }

    @Override
    public Message findMessage(int messageNumber) {
        return messages.stream()
                .filter(m -> m.getNumber() == messageNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAllMessage() {
        return messages;
    }
}