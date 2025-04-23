package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
@Repository
public class FileMessageRepository implements MessageRepository {
    FilePathUtil pathUtil = new FilePathUtil();
    FileSerializer serializer = new FileSerializer();

    @Override
    public Message createMessageByUserIdAndChannelId(UUID senderId, UUID channelId, String content) {
        Message message = new Message(senderId, channelId, content);
        Path path = pathUtil.getMessageFilePath(message.getId());
        serializer.writeFile(path, message);
        return message;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Path path = pathUtil.getMessageFilePath(messageId);
        return serializer.readFile(path, Message.class);
    }

    @Override
    public List<Message> findAllMessages() {
        Path directory = pathUtil.getMessageDirectory();

        if (!Files.exists(directory)) {
            return new ArrayList<>();
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
        Path path = pathUtil.getMessageFilePath(messageId);
         if (!Files.exists(path)) {
            throw new RuntimeException("no file: FileMessageRepository.updateMessageById");
        }
        Message message = serializer.readFile(path, Message.class);
        message.setContent(content);
        serializer.writeFile(path, message);
    }

    @Override
    public void deleteMessageById(UUID messageId) {
        Path path = pathUtil.getMessageFilePath(messageId);
        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("problem while deleting",e);
        }
    }
}
