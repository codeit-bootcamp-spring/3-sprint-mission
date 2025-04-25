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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    //
    private final UserService userService;
    private final ChannelService channelService;


    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        //  현재디렉토리/data/userDB 디렉토리를 저장할 path로 설정
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", Message.class.getSimpleName());
        //  지정한 path에 디렉토리 없으면 생성
        if (!Files.exists(this.DIRECTORY)) {
            try {
                Files.createDirectories(this.DIRECTORY);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message create(Message message) {
        Channel channel;
        try {
            userService.find(message.getUserId());
            channel = this.channelService.find(message.getChannelId());
        } catch (NoSuchElementException e) {
            throw e;
        }

        // 객체를 저장할 파일 path 생성
        Path filePath = this.resolvePath(message.getId());

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {

            oos.writeObject(message);

            this.channelService.addMessageToChannel(channel.getId(), message.getId());

            return message;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message find(UUID messageId) {
        // 객체가 저장된 파일 path
        Path filePath = resolvePath(messageId);

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            Message message = (Message) ois.readObject();
            return message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try {
            Files.list(this.DIRECTORY).filter(Files::isRegularFile)
                    .forEach((path) -> {
                        try ( // 파일과 연결되는 스트림 생성
                              FileInputStream fis = new FileInputStream(String.valueOf(path));
                              // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                              ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            Message message = (Message) ois.readObject();
                            messages.add(message);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });

            return messages;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Message update(UUID messageId, String newContent, List<UUID> attachmentIds) {

        try {
            Message message = this.find(messageId);
            message.update(newContent, attachmentIds);
            this.create(message);
            return message;
        } catch (RuntimeException e) {
            throw e;
        }

    }

    //해당 채널의 메세지들을 다 읽음
    @Override
    public void delete(UUID messageId) {
        // 객체가 저장된 파일 path
        Path filePath = resolvePath(messageId);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {

        try {
            Channel channel = this.channelService.find(channelId);
            List<UUID> messageIds = channel.getMessages();

            List<Message> messages = new ArrayList<>();
            messageIds.forEach((messageId) -> {
                messages.add(this.find(messageId));
            });

            return messages;
        } catch (NoSuchElementException e) {
            throw e;
        }
    }

}
