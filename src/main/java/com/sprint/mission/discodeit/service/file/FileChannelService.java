package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    //
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.userService = userService;

        //  현재디렉토리/data/channelDB 디렉토리를 저장할 path로 설정
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", Channel.class.getSimpleName());
        //  지정한 path에 디렉토리 없으면 생성
        if (!Files.exists(this.DIRECTORY)) {

            try {
                Files.createDirectories(this.DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        // TODO : 예외타입 변경하기

    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel create(Channel channel) {
        // 객체를 저장할 파일 path 생성
        Path filePath = resolvePath(channel.getId());

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Channel find(UUID channelId) {
        // 객체가 저장된 파일 path
        Path filePath = this.DIRECTORY.resolve(String.valueOf(channelId).concat(".ser"));

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            Channel channelNullable = (Channel) ois.readObject();
            return Optional.ofNullable(channelNullable).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();

        try {
            Files.list(this.DIRECTORY).filter(Files::isRegularFile)
                    .forEach((path) -> {

                        try (   // 파일과 연결되는 스트림 생성
                                FileInputStream fis = new FileInputStream(String.valueOf(path));
                                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                                ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            Channel channel = (Channel) ois.readObject();
                            channels.add(channel);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    });

            return channels;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {

        try {
            Channel channel = this.find(channelId);
            channel.update(newName, newDescription);
            this.create(channel);
            return channel;
        } catch (RuntimeException e) {
            throw e;
        }

    }

    @Override
    public void delete(UUID channelId) {
        // 객체가 저장된 파일 path
        Path filePath = resolvePath(channelId);

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
    public void addMessageToChannel(UUID channelId, UUID messageId) {

        try {
            Channel channel = this.find(channelId);
            channel.addMessage(messageId);
            this.create(channel);
        } catch (RuntimeException e) {
            throw e;
        }

    }

    @Override
    public void addAttendeeToChannel(UUID channelId, UUID userId) {

        try {
            Channel channel = this.find(channelId);
            channel.addAttendee(userId);
            this.create(channel);
        } catch (RuntimeException e) {
            throw e;
        }

    }

    @Override
    public void removeAttendeeToChannel(UUID channelId, UUID userId) {
        //REF : equals와 hashCode 재정의
        try {
            Channel channel = this.find(channelId);
            channel.removeAttendee(userId);
            this.create(channel);
        } catch (RuntimeException e) {
            throw e;
        }

    }

    @Override
    public List<User> findAttendeesByChannel(UUID channelId) {

        try {
            Channel channel = this.find(channelId);

            List<User> attendees = new ArrayList<>();
            channel.getAttendees().forEach((userId -> {
                attendees.add(this.userService.find(userId));
            }));

            return attendees;
        } catch (RuntimeException e) {
            throw e;
        }

    }

}
