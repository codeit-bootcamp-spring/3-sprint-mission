package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
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

    private static final Path FILE_PATH = Paths.get("src/main/java/com/sprint/mission/discodeit/repository/file/data/messages.ser");


    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<Message> readFiles() {
        try {
            if (!Files.exists(FILE_PATH) || Files.size(FILE_PATH) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Message> messages = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))) {
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
            Files.createDirectories(FILE_PATH.getParent());
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))) {
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
        List<Message> deleteMessages = messages.stream()
                .filter((c) -> !c.getId().equals(messageId))
                .toList();
        writeFiles(deleteMessages);
    }
}
