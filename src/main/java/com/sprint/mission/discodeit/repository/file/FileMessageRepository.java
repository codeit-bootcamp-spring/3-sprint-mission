package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), directory, Message.class.getSimpleName());
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Message 디렉토리 생성 오류", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(resolvePath(message.getId()).toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("Message 저장 오류", e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return Optional.of((Message) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Message 읽기 오류", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Message 목록 읽기 오류", e);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("Message 디렉토리 탐색 오류", e);
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId).forEach(m -> deleteById(m.getId()));
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Message 삭제 오류", e);
        }
    }
}