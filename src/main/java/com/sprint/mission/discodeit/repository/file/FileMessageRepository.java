package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Profile("file")
public class FileMessageRepository implements MessageRepository {
    private final Path path;

    public FileMessageRepository(@Value("${storage.dirs.messages}") String dir) {
        this.path = Paths.get(dir);
        clearFile();
    }

    @Override
    public void save(Message message) {
        String filename = message.getId().toString() + ".ser";
        Path file = path.resolve(filename);

        try (
                OutputStream out = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message loadById(UUID id) {
        Path file = path.resolve(id.toString() + ".ser");
        return deserialize(file);
    }

    @Override
    public List<Message> loadAll() {
        List<Message> messages = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.ser")) {
            for (Path p : stream) {
                messages.add(deserialize(p));
            }
        } catch (IOException e) {
            throw new RuntimeException("[Message] messages 폴더 읽기 실패", e);
        }

        return messages;
    }

    @Override
    public List<Message> loadByChannelId(UUID channelId) {
        List<Message> messages = loadAll();

        return messages.stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void update(UUID id, String content) {
        Message msg = loadById(id);
        if (msg == null) {
            throw new IllegalArgumentException("[Message] 유효하지 않은 메시지입니다. (" + id + ")");
        }

        msg.update(content);
        save(msg);
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Path deletePath = path.resolve(id + ".ser");
            Files.deleteIfExists(deletePath);

        } catch (IOException e) {
            throw new RuntimeException("[Message] 파일 삭제 실패 (" + id + ")", e);
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        loadByChannelId(channelId).stream()
                .map(Message::getId)
                .forEach(this::deleteById);
    }

    private Message deserialize(Path file) {
        if (Files.notExists(file)) {
            throw new IllegalArgumentException("[Message] 유효하지 않은 파일");
        }

        try (
                InputStream in = Files.newInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(in)
        ) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Message] Message 파일 로드 실패", e);
        }
    }

    private void clearFile() {
        try {
            if (Files.exists(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path filePath : stream) {
                        Files.deleteIfExists(filePath);
                    }
                }
            } else {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
