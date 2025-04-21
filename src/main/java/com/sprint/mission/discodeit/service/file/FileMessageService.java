package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private final Path messageDirectory;

    public FileMessageService() {
        this.messageDirectory = Paths.get(System.getProperty("user.dir"), "data", "messages");
        init();
    }

    private void init() {
        try {
            if (!Files.exists(messageDirectory)) {
                Files.createDirectories(messageDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 디렉토리 생성 실패", e);
        }
    }

    @Override
    public Message create(Message message) {
        save(message);
        return message;
    }

    @Override
    public Message getById(UUID id) {
        Path filePath = messageDirectory.resolve(id.toString().concat(".ser"));
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Message> getAll() {
        if (!Files.exists(messageDirectory)) return new ArrayList<>();

        try {
            return Files.list(messageDirectory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message update(Message message) {
        return create(message);
    }

    @Override
    public void delete(UUID id) {
        Path filePath = messageDirectory.resolve(id.toString().concat(".ser"));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(Message message) {
        Path filePath = messageDirectory.resolve(message.getId().toString().concat(".ser"));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
