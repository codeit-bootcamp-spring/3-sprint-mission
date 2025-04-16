package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final String MESSAGE_FILE_REPOSITORY_PATH = "src/main/java/com/sprint/mission/discodeit/repository/data/Message.txt";
    File file = new File(MESSAGE_FILE_REPOSITORY_PATH);

    @Override
    public void save(Message message) {

        // 방어 코드
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(List<Message> messages) {

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }


        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> loadAll() {
        // 객체 역직렬화
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Message loadById(UUID id) {
        List<Message> messages = loadAll();
        for (Message message : messages) {
            if (message.getMessageId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> loadByType(String type) {
        List<Message> messages = loadAll();
        for (Message message : messages) {
            if (message.getMessageType().equals(type)) {
                return messages;
            }
        }
        return null;
    }
}
