package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
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
    }

    private Path resolvePath(UUID id) {
        return this.DIRECTORY.resolve(id + EXTENSION);
    }


    @Override
    public Channel save(Channel channel) {
        // 객체를 저장할 파일 path 생성
        Path filePath = this.resolvePath(channel.getId());

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
    public Optional<Channel> findById(UUID channelId) {
        Path filePath = this.resolvePath(channelId);

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            Channel channelNullable = (Channel) ois.readObject();
            return Optional.ofNullable(channelNullable);
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
                            //FIXME
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    });

            return channels;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID channelId) {
        // 객체가 저장된 파일 path
        Path filePath = this.resolvePath(channelId);

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

}
