package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final Path databasePath;


    public FileChannelService() {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Channel ch) {
        // 객체를 저장할 파일 path 생성
        Path filePath = this.databasePath.resolve(String.valueOf(ch.getId()).concat(".ser"));
        // 파일 생성
        File myObj = new File(String.valueOf(filePath));

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(myObj);
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {

            oos.writeObject(ch);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel read(UUID id) {
        Path filePath = this.databasePath.resolve(String.valueOf(id).concat(".ser"));

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
    public List<Channel> readAll() {
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
                            //FIXME
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return channels;
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = this.read(id);
        Channel updatedChannel = channel.update(name);
        this.create(updatedChannel);
        return channel;
    }

    @Override
    public boolean delete(UUID id) {
        Path filePath = this.databasePath.resolve(String.valueOf(id).concat(".ser"));
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> getAttendees(Channel ch) {
        Channel selectedChannel = this.read(ch.getId());
        return selectedChannel.getAttendees();
    }

    @Override
    public User joinChannel(Channel ch, User user) {
        Channel selectedChannel = this.read(ch.getId());
        selectedChannel.getAttendees().addAll(Arrays.asList(user));
        this.create(selectedChannel);
        return user;
    }

    @Override
    public User leaveChannel(Channel ch, User user) {
        Channel selectedChannel = this.read(ch.getId());
        //REF : equals와 hashCode 재정의
        selectedChannel.getAttendees().remove(user);

        this.create(selectedChannel);
        return user;
    }

    @Override
    public List<User> readAttendees(Channel ch) {
        Channel selectedChannel = this.read(ch.getId());
        return selectedChannel.getAttendees();
    }
}
