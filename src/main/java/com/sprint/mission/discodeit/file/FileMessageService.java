package com.sprint.mission.discodeit.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private static final String FILE_PATH = "messages.ser";
    private Map<UUID, Message> messages = load();

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        UUID id = UUID.randomUUID(); // 🔥 메시지 ID 생성
        Message message = new Message(id, channelId, content, authorId); // ✅ 올바른 순서로 생성자 호출
        messages.put(message.getId(), message); // Map에 저장
        save(); // 파일에도 저장
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public Message update(UUID id, String newContent) {
        Message message = messages.get(id);
        if (message != null) {
            message.updateContent(newContent);
            save();
        }
        return message;
    }

    @Override
    public void delete(UUID id) {
        messages.remove(id);
        save();
    }

    private void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, Message> load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Message>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}
