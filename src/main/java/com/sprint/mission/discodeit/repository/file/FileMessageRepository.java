package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

@Repository
public class FileMessageRepository implements MessageRepository {

    private final Path dataDirectory;

    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), baseDir, "messages");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("메시지 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getMessagePath(UUID messageId) {
        return dataDirectory.resolve(messageId.toString() + ".ser");
    }

    private void saveMessage(Message message) {
        Path messagePath = getMessagePath(message.getMessageId());
        try (FileOutputStream fos = new FileOutputStream(messagePath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패: " + message.getMessageId(), e);
        }
    }

    private Message loadMessage(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 로드 실패: " + path, e);
        }
    }

    @Override
    public Message save(Message message) {
        saveMessage(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        Path messagePath = getMessagePath(messageId);
        if (Files.exists(messagePath)) {
            return Optional.of(loadMessage(messagePath));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadMessage)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("메시지 목록 로드 실패", e);
        }
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return findAll().stream()
                .filter(m -> m.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId) {
        return findByChannelId(channelId).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID messageId) {
        try {
            Files.deleteIfExists(getMessagePath(messageId));
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + messageId, e);
        }
    }

    @Override
    public Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt));
    }
}
