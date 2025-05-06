package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileMessageRepository implements MessageRepository {

    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "message");

    public FileMessageRepository() {
        init();
    }

    // 저장할 경로의 파일 초기화
    public static Path init() {
        if (!Files.exists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return DIRECTORY;
    }

    public static Message load(Path filePath){
        if (!Files.exists(filePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (Message) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public Message save(Message message) {
        Path filePath = Paths.get(String.valueOf(DIRECTORY), message.getId()+".ser");
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        if (!Files.exists(DIRECTORY)) {
            return new ArrayList<>();
        } else {
            List<Message> data = new ArrayList<>();
            File[] files = DIRECTORY.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    data.add(load(file.toPath()));
                }
            }
            return data;
        }
    }

    @Override
    public Message find(UUID id) {
        return load(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public List<Message> findAllFromChannel(UUID currentChannel) {

        return findAll().stream()
                .filter(m -> m.getChannelId().equals(currentChannel))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByText(String text) {

        return findAll().stream()
                .filter(m -> m.getText().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.delete(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }
}
