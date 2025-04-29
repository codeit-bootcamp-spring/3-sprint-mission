package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {

    private final Path dir = Paths.get(System.getProperty("user.dir"), "data", "messages");

    public FileMessageRepository() {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 디렉토리 생성 실패", e);
        }
    }

    @Override
    public Message save(Message message) {
        Path filePath = dir.resolve(message.getId().toString().concat(".ser"));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패", e);
        }
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Path filePath = dir.resolve(id.toString().concat(".ser"));
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 로딩 실패", e);
        }
    }

    @Override
    public List<Message> findAll() {
        if (!Files.exists(dir)) return new ArrayList<>();

        try {
            return Files.list(dir)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("메시지 로딩 실패", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("메시지 목록 불러오기 실패", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        Path filePath = dir.resolve(id.toString().concat(".ser"));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패", e);
        }
    }
}
