package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "messageRepo.ser";
    private final Map<UUID, Message> data = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        data.putAll(loadFile());
    }

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);

        saveFile();

        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        Optional<Message> foundMessage = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(messageId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundMessage;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        return data.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findMessageByContainingWord(String word) {
        return data.values().stream()
                .filter(message -> message.getContent().contains(word))
                .toList();
    }

    @Override
    public void deleteById(UUID messageId) {
        data.remove(messageId);
        // Message 삭제 후 파일에 덮어쓰기
        saveFile();
    }

    private Map<UUID, Message> loadFile() {
        Map<UUID, Message> data = new HashMap<>();

        if (getFile().exists()) {
            try (FileInputStream fis = new FileInputStream(getFile());
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, Message>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private synchronized void saveFile() {
        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
