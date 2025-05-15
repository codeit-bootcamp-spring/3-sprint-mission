package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.helper.FilePathProperties;
import com.sprint.mission.discodeit.helper.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.file
 * fileName       : FileMessageRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
@RequiredArgsConstructor
public class FileMessageRepository implements MessageRepository {
    private final FilePathProperties pathUtil;



    @Override
    public Message createMessageWithAttachments(UUID userId, UUID channelId, List<UUID> attachments) {
        Message message = new Message(userId, channelId, attachments);
        Path path = pathUtil.getMessageFilePath(message.getId());
        FileSerializer.writeFile(path, message);
        return message;
    }

    @Override
    public Message createMessageWithContent(UUID senderId, UUID channelId, String content) {
        Message message = new Message(senderId, channelId, content);
        Path path = pathUtil.getMessageFilePath(message.getId());
        FileSerializer.writeFile(path, message);
        return message;
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        Path directory = pathUtil.getMessageDirectory();

        if (!Files.exists(directory)) {
            return Collections.emptyList();
        }

        List<Message> messageList;
        try {
            messageList = Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Message) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileReadStatusRepository.findReadStatusByChannelId", exception);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("리스트로 만드는 과정에 문제 발생: FileReadStatusRepository.findReadStatusByChannelId",e);
        }

        List<Message> selectedMessage = new ArrayList<>();
        for (Message message : messageList) {
            if (message.getChannelId().equals(channelId)) {
                selectedMessage.add(message);
            }
        }
        return selectedMessage;
    }



    @Override
    public Message findMessageById(UUID messageId) {
        Path path = pathUtil.getMessageFilePath(messageId);
        if (!Files.exists(path)) {
            return null;
        }
        return FileSerializer.readFile(path, Message.class);
    }

    @Override
    public List<Message> findAllMessages() {
        Path directory = pathUtil.getMessageDirectory();

        if (!Files.exists(directory)) {
            return Collections.emptyList();
        }

        try {
            return Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Message) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("problem with reading: FileMessageRepository.findAllMessages",exception);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("problem with creating List: FileMessageRepository.findAllMessages",e);
        }
    }

    @Override
    public void updateMessageById(UUID messageId, String content) {
        Objects.requireNonNull(messageId, "no messageId in param");
        Objects.requireNonNull(content, "no content in param");

        Path path = pathUtil.getMessageFilePath(messageId);
        if (!Files.exists(path)) {
            throw new IllegalStateException("no file: FileMessageRepository.updateMessageById");
        }
        Message message = FileSerializer.readFile(path, Message.class);
        message.setContent(content);
        FileSerializer.writeFile(path, message);
    }

    @Override
    public void deleteMessageById(UUID messageId) {
        Objects.requireNonNull(messageId, "no messageId in param");
        Path path = pathUtil.getMessageFilePath(messageId);
        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("problem while deleting",e);
        }
    }
}
