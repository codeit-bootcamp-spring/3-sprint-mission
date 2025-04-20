package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final Path directory;

    public FileMessageRepository(Path directory) {
        this.directory = directory;
        initDirectory();
    }

    private void initDirectory() {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패: ", e);
        }
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id.toString().concat(".ser"));
    }

    private Message saveFile(Message message) {
        try (
                FileOutputStream fos = new FileOutputStream(resolvePath(message.getId()).toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패: " + e);
        }
    }

    private Optional<Message> loadFile(UUID id) {
        if (Files.exists(resolvePath(id))) {
            try (
                    FileInputStream fis = new FileInputStream(resolvePath(id).toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                return Optional.of((Message) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Message save(Message message) {
        return saveFile(message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return loadFile(id);
    }

    @Override
    public List<Message> findAll() {
        if (Files.exists(directory)) {
            try {
                return Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                return (Message) ois.readObject();
                            } catch (IOException | ClassNotFoundException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException("메시지 전체 조회 실패", e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + id, e);
        }
    }
}