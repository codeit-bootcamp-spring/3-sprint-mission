package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private String fileName;
    private File file;

    public FileMessageRepository(String filePath) {
        this.fileName = "src/main/java/com/sprint/mission/discodeit/" + filePath + "/messageRepo.ser";
        this.file = new File(fileName);
    }

    @Override
    public Message save(Message message) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = loadFile();

        data.put(message.getId(), message);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = loadFile();

        Optional<Message> foundMessage = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(messageId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundMessage;
    }

    @Override
    public List<Message> findAll() {
        // 파일에서 읽어오기
        Map<UUID, Message> data = loadFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = loadFile();

        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findMessagesByUserId(UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = loadFile();

        return data.values().stream()
                .filter(message -> message.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findMessageByContainingWord(String word) {
        // 파일에서 읽어오기
        Map<UUID, Message> data = loadFile();

        return data.values().stream()
                .filter(message -> message.getContent().contains(word))
                .toList();
    }

    @Override
    public void deleteById(UUID messageId) {
        Map<UUID, Message> data = loadFile();

        data.remove(messageId);

        // Message 삭제 후 파일에 덮어쓰기
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, Message> loadFile() {
        Map<UUID, Message> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, Message>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
