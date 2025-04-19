package com.sprint.mission.discodeit.v1.repository.file;

import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.repository.MessageRepository1;
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
public class FileMessageRepository1 implements MessageRepository1 {
    FilePathUtil filePathUtil;
    FileSerializer fileSerializer;

    public FileMessageRepository1(FilePathUtil filePathUtil, FileSerializer fileSerializer) {
        this.filePathUtil = filePathUtil;
        this.fileSerializer = fileSerializer;
    }

    @Override
    public Message1 saveMessage(UUID senderId, UUID channelId, String message) {
        Message1 msg = new Message1(senderId, channelId, message);
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
    public List<Message1> findAllMessages() {
        Path directory = filePathUtil.getMessageDirectory();

        if (Files.exists(directory)) {
            try {
                List<Message1> list = Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (Message1) data;
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
    public Message1 findMessageById(UUID messageId) {
        Path path = filePathUtil.getMessageFilePath(messageId);

        if (Files.exists(path)) {
            return fileSerializer.readFile(path, Message1.class);

        }
        return null;
    }
    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        Path path = filePathUtil.getMessageFilePath(messageId);

        // 메세지 읽어오기
        if (Files.exists(path)) {
            Message1 message = fileSerializer.readFile(path, Message1.class);
            message.setMessage(newMessage);
            fileSerializer.writeFile(path,message);

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
        List<Message1> allMessages = findAllMessages();

        for (Message1 message : allMessages) {
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



