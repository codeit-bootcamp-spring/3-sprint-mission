package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class FileMessageService implements MessageService {

    private final Path dataDirectory;
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), "data", "messages");
        this.userService = userService;
        this.channelService = channelService;
        init();
    }

    private void init() { // 사용자 데이터 디렉토리 초기화
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("사용자 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getMessagePath(UUID messageId) {
        return dataDirectory.resolve(messageId.toString() + ".ser");
    }

    private void saveMessage(Message message) {
        Path messagePath = getMessagePath(message.getMessageId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(messagePath.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패: " + message.getMessageId(), e);
        }
    }

    private Message loadMessage(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 로드 실패: " + path, e);
        }
    }

    @Override
    public Message createMessage(String content, UUID authorId, UUID channelId) {
        // 작성자 존재 여부 확인
        if (userService.getUserById(authorId) == null) {
            throw new IllegalArgumentException("존재하지 않는 작성자 ID입니다.");
        }
        // 채널 존재 여부 확인
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID입니다.");
        }
        // 작성자가 채널 참가자인지 확인
        if (!channel.isParticipant(authorId)) {
            throw new IllegalStateException("채널 참가자만 메시지를 작성할 수 있습니다.");
        }

        Message message = new Message(content, authorId, channelId);
        saveMessage(message);
        return message;
    }

    @Override
    public Message getMessageById(UUID messageId) {
        Path messagePath = getMessagePath(messageId);
        if (Files.exists(messagePath)) {
            return loadMessage(messagePath);
        }
        return null;
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadMessage)
                    .filter(message -> message.getChannelId().equals(channelId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("채널 메시지 목록 로드 실패", e);
        }
    }

    @Override
    public List<Message> getMessagesByAuthor(UUID authorId) {
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadMessage)
                    .filter(message -> message.getAuthorId().equals(authorId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("작성자 메시지 목록 로드 실패", e);
        }
    }

    @Override
    public void updateMessage(UUID messageId, String updatedContent) {
        Message message = getMessageById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다");
        }
        message.updateContent(updatedContent);
        saveMessage(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = getMessageById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다");
        }
        try {
            Files.deleteIfExists(getMessagePath(messageId));
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + messageId, e);
        }
    }
}
