package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "File")
public class FileMessageRepository implements MessageRepository {

    @Value( "${discodeit.repository.fileDirectory}")
    private String FILE_Directory;
    private final String FILE_NAME = "message.ser";

    public Path getFilePath() {
        return Paths.get(FILE_Directory, FILE_NAME);
    }

    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<Message> readFiles() {
        try {
            if (!Files.exists(getFilePath()) || Files.size(getFilePath()) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Message> messages = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(getFilePath().toFile()))) {
            while(true) {
                try {
                    messages.add((Message) reader.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }


    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<Message> messages) {
        try {
            Files.createDirectories(getFilePath().getParent());
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(getFilePath().toFile()))) {
                for (Message message : messages) {
                    writer.writeObject(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Message save(Message message) {
        List<Message> messages = readFiles();
        messages.add(message);
        writeFiles(messages);
        return message;
    }

    @Override
    public List<Message> read() {
        List<Message> messages = readFiles();
        return messages;
    }

    @Override
    public List<Message> readByChannelId(UUID channelId) {
        List<Message> messages = readFiles();
        List<Message> channelMessages = messages.stream()
                .filter((c)->c.getChannelId().equals(channelId))
                .collect(Collectors.toList());
        return channelMessages;
    }

    @Override
    public Optional<Message> readById(UUID id) {
        List<Message> messages = readFiles();
        Optional<Message> message = messages.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return message;
    }

    @Override
    public void update(UUID id, Message message) {
        List<Message> messages = readFiles();
        messages.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                    c.setText(message.getText());
                    c.setAttachmentIds(message.getAttachmentIds());
                });
        writeFiles(messages);
    }

    @Override
    public void delete(UUID messageId) {
        List<Message> messages = readFiles();
        messages.removeIf(message -> message.getId().equals(messageId));
        writeFiles(messages);
    }
}
