package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileMessageRepository implements MessageRepository {
    private static Map<UUID, List<Message>> channelMsgBoard = new ConcurrentHashMap<>();
    private static final String MESSAGE_FILE_PATH = "src/files/";
    private static final String MESSAGE_FILE_PRIMER = "message_";
    private static final String MESSAGE_FILE_TAIL = ".ser";

    public FileMessageRepository() {
    }
    // 파일 로드 메서드
    @Override
    public List<Message> getMessages(UUID channelId) {
        if (!channelMsgBoard.containsKey(channelId)) {
            channelMsgBoard.put(channelId, fileLoadMessages(channelId));
        }
        return channelMsgBoard.get(channelId);
    }

    @Override
    public void saveMessages(UUID channelId, List<Message> messages) {
        channelMsgBoard.put(channelId, messages);
        String filePath = getFilePath(channelId);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> fileLoadMessages (UUID channelId) {
        String filePath = getFilePath(channelId);
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private String getFilePath(UUID channelId) {
        String filePath = MESSAGE_FILE_PATH + MESSAGE_FILE_PRIMER + channelId.toString() + MESSAGE_FILE_TAIL;
        return filePath;
    }
}
