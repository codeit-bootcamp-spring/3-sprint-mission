package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final Path databasePath;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.userService = userService;

        try {
            //  현재디렉토리/data/channelDB 디렉토리를 저장할 path로 설정
            this.databasePath = Paths.get(System.getProperty("user.dir"), "data", "channelDB");
            //  지정한 path에 디렉토리 없으면 생성
            if (!Files.exists(this.databasePath)) {

                try {
                    Files.createDirectories(this.databasePath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            // TODO : 예외타입 변경하기
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Channel create(Channel channel) {
        // 객체를 저장할 파일 path 생성
        Path filePath = this.databasePath.resolve(String.valueOf(channel.getId()).concat(".ser"));
        // 파일 생성
        File myObj = new File(String.valueOf(filePath));

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(myObj);
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
            return channel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Channel find(UUID channelId) {
        // 객체가 저장된 파일 path
        Path filePath = this.databasePath.resolve(String.valueOf(channelId).concat(".ser"));

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            Channel channel = (Channel) ois.readObject();
            return channel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();

        try {
            Files.walk(this.databasePath).filter(Files::isRegularFile)
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
        } catch (Exception e) {
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
        Path filePath = this.databasePath.resolve(String.valueOf(channelId).concat(".ser"));

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
