package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private static final String MESSAGE_FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/data/message.txt";

    // 직렬화 & 역직렬화 코드 작성
    // 오버라이드
    // 메세지 불러오기
    private List<Message> loadMessages() {
        File file = new File(MESSAGE_FILE_PATH);
        if (!file.exists()) {     // 방어 코드
            return new ArrayList<>();     // 새로운 ArrayList 반환
        }

        // 객체 역직렬화
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();     // 예외처리 로그 출력
            return new ArrayList<>();     // 예외 발생 시 빈 리스트 반환
        }
    }


    // 리스트 저장
    private void saveMessage(List<Message> messages) {

        // 객체 직렬화
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MESSAGE_FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();     // 예외처리 로그 출력
        }
    }


    @Override
    public void createMessage(Message message) {
        List<Message> messages = loadMessages();
        messages.add(message);
        saveMessage(messages);

    }

    @Override
    public Message readMessage(UUID id) {
        return loadMessages().stream()
                .filter(message -> message.getMessageId().equals(id))
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<Message> readMessageByType(String messageType) {
        return loadMessages().stream()
                .filter(message -> message.getMessageType().equalsIgnoreCase(messageType))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> readAllMessages() {
        return loadMessages();
    }

    @Override
    public Message updateMessage(UUID id, Message message) {
        List<Message> messages = loadMessages();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getMessageId().equals(id)) {
                messages.set(i, message);
                saveMessage(messages);
                return message;
            }
        }
        return null;
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        List<Message> messages = loadMessages();
        boolean removed = messages.removeIf(message -> message.getMessageId().equals(messageId));
        if (removed) {
            saveMessage(messages);
            System.out.println("[INFO] Deleted Message : " + messageId);
        } else {
            System.out.println("[ERROR] Message not found");
        }
        return removed;
    }
}
