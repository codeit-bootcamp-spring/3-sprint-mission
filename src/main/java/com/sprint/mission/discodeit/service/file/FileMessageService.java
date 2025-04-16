package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/message.txt";
    private final Path path = Paths.get(FILE_PATH);

    private Map<UUID, Message> data;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this.data = new HashMap<>();
    }

    // 직렬화 : 생성
    public void saveMessage(List<Message> messages) { // 객체 직렬화
        try ( // 길 뚫어주고
              FileOutputStream message = new FileOutputStream(FILE_PATH); // file 주소를 어떻게 설정할까
              ObjectOutputStream messageOOS = new ObjectOutputStream(message);
              // ObjectOutputStream messageOOS = new ObjectOutputStream(new FileOutputStream(new File("message.ser")));
        ) {
            messageOOS.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화 : 조회
    public List<Message> loadMessages(Path path) {
        List<Message> messages = new ArrayList<>();

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (
                ObjectInputStream messageOIS = new ObjectInputStream(new FileInputStream(FILE_PATH));
        ) {
            for (Message message : (List<Message>) messageOIS.readObject()) {
                messages.add(message);
            }
            return messages;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public Message createMessage(String content, UUID channelId, UUID authorId) {
        Message message = new Message(content, channelId, authorId); // 파라미터로 받아온 user의 정보로 객체 직접 생성
        this.data.put(message.getId(), message);

        List<Message> messages = new ArrayList<>(data.values());

        saveMessage(messages);

        return message;
    }

    @Override
    public Message readMessage(UUID id) { // R
        List<Message> messages = loadMessages(path); // 유저 리스트 역직렬화 // 객체 하나하나 역직렬화 아님

        for (Message message : messages) {
            if (message.getId().equals(id)) {
                System.out.println("메시지 정보 출력");
                System.out.println("===================");
                System.out.println("채널ID: " + message.getChannelId());
                System.out.println("작성자ID: " + message.getAuthorId());
                return message;
            }
        }

        System.out.println("해당 ID를 가진 메시지를 찾을 수 없습니다.");
        return null; // 없으면 null, 예외 처리는 아직 못했음
    }

    @Override
    public List<Message> readAllMessages() { // R 전체 조회
        return loadMessages(path);
    }

    @Override
    public Message updateMessage(UUID id, String newContent) { // U
        Message messageNullable = this.data.get(id);
        Message message = Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 메시지를 찾을 수 없습니다."));
        message.updateMessage(newContent);

        saveMessage(this.data.values().stream().toList());

        return message;
    }

    @Override
    public void deleteMessage(UUID id) { // D
        if (!this.data.containsKey(id)) {
            throw new NoSuchElementException(id + "ID를 가진 메시지를 찾을 수 없습니다.");
        }
        this.data.remove(id);
        saveMessage(this.data.values().stream().toList());
    }
}
