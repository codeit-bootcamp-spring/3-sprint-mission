package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path databasePath;

    public FileChannelRepository() {

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
    public Channel write(Channel channel) {
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
    public Channel read(UUID channelId) {
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

}
