package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageService implements MessageService {
    private Path directory;

    private void initDirectory() {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패: ", e);
        }
    }

    public FileMessageService(Path directory) {
        this.directory = directory;
        initDirectory();
    }

    private Path resolvePath(UUID messageId) {
        return directory.resolve(messageId.toString().concat(".ser"));
    }

    // 저장 로직
    private void saveFile(Message message) {
        try (
                FileOutputStream fos = new FileOutputStream(resolvePath(message.getId()).toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패: " + e);
        }
    }

    // 저장 로직
    private Optional<Message> loadFile(UUID messageId) {
        if (Files.exists(resolvePath(messageId))) {
            try (
                    FileInputStream fis = new FileInputStream(resolvePath(messageId).toFile());
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

    // 비즈니스 로직
    @Override
    public Message createMessage(Message message) {
        saveFile(message);
        return message;
    }

    // 비즈니스 로직
    @Override
    public Optional<Message> getMessage(UUID messageId) {
        return loadFile(messageId);
    }

    // 비즈니스 로직
    @Override
    public List<Message> getAllMessages() {
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
                                throw new RuntimeException("메시지 파일 로딩 실패: " + path, e);
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

    // 비즈니스 로직
    @Override
    public void updateMessage(UUID messageId, String msgContent) {
        getMessage(messageId).ifPresent(m -> {
            m.updateMsgContent(msgContent);
            saveFile(m);
        });
    }

    // 비즈니스 로직
    @Override
    public void deleteMessage(UUID messageId) {
        try {
            Files.deleteIfExists(resolvePath(messageId));
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + messageId, e);
        }
    }
}
