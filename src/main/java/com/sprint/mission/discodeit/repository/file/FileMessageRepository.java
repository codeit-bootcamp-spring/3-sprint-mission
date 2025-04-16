package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileMessageRepository
 * author         : doungukkim
 * date           : 2025. 4. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 15.        doungukkim       최초 생성
 */
public class FileMessageRepository implements MessageRepository {
    FilePathUtil filePathUtil;
    FileSerializer fileSerializer;

    public FileMessageRepository(FilePathUtil filePathUtil, FileSerializer fileSerializer) {
        this.filePathUtil = filePathUtil;
        this.fileSerializer = fileSerializer;
    }

    @Override
    public Message saveMessage(UUID senderId, UUID channelId, String message) {
        Message msg = new Message(senderId, channelId, message);
        Path path = filePathUtil.getMessageFilePath(msg.getId());

        if (!Files.exists(path)) {
            try (FileOutputStream fos = new FileOutputStream(path.toFile());
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                oos.writeObject(msg);
                return msg;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    @Override
    public List<Message> findAllMessages() {
        Path directory = filePathUtil.getMessageDirectory();

        if (Files.exists(directory)) {
            try {
                List<Message> list = Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Message) data;
                            } catch (IOException | ClassNotFoundException exception) {
                                throw new RuntimeException(exception);
                            }
                        }).toList();
                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }
    @Override
    public Message findMessageById(UUID messageId) {
        Path path = filePathUtil.getMessageFilePath(messageId);

        if (Files.exists(path)) {
            return fileSerializer.readObject(path, Message.class);

        }
        return null;
    }
    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        Path path = filePathUtil.getMessageFilePath(messageId);

        // 메세지 읽어오기
        if (Files.exists(path)) {
            Message message = fileSerializer.readObject(path, Message.class);
            message.setMessage(newMessage);
            fileSerializer.writeObject(path,message);

        }
    }
    @Override
    public void deleteMessageById(UUID messageId) {
        Path path = filePathUtil.getMessageFilePath(messageId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        List<Message> allMessages = findAllMessages();

        for (Message message : allMessages) {
            if (message.getChannelId().equals(channelId)) {
                try {
                    Files.delete(filePathUtil.getMessageFilePath(message.getId()));
                } catch (IOException e) {
                    System.out.println("메세지 삭제 실패");
                    throw new RuntimeException(e);
                }
            }
        }
    }
}



