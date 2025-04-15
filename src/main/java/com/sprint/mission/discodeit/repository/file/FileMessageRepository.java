package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.util.FilePathUtil;

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
public class FileMessageRepository {
    FilePathUtil filePathUtil = new FilePathUtil();

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

    public Message findMessageByMessageId(UUID messageId) {
        Path path = filePathUtil.getMessageFilePath(messageId);
        Message message;

        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                message = (Message) ois.readObject();
                return message;

            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void updateMessage(UUID messageId, String newMessage) {
        Path path = filePathUtil.getMessageFilePath(messageId);
        Message message;
        // 메세지 읽어오기
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                message = (Message) ois.readObject();
                message.setMessage(newMessage);
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                    oos.writeObject(message);
                }
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public boolean deleteMessageById(UUID messageId) {
        Path path = filePathUtil.getMessageFilePath(messageId);
        UUID channelId = findMessageByMessageId(messageId).getChannelId();
        try {
            Files.delete(path);
            return true;
            // channel 안의 메세지 관리(삭제)
//            channelService.deleteMessageInChannel(channelId, messageId);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteMessagesByPath(Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            System.out.println("메세지 삭제 실패");
            throw new RuntimeException(e);
        }
    }









}



