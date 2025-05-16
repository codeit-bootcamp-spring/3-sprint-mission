package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;


@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository implements ChannelRepository {

    private static final Logger logger = Logger.getLogger(FileChannelRepository.class.getName());

//    private final Map<UUID, Channel> channels = loadChannelFromFile();

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";


    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    // 채널 저장
    @Override
    public Channel saveChannel(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("FileChannelRepository: 채널 파일 저장 중 오류 발생 ", e);
        }
        return channel;
    }

    // 전체 채널 조회
    @Override
    public List<Channel> findAllChannels() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("FileChannelRepository: 채널 파일 읽기 중 오류 발생 ", e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("FileChannelRepository: 채널 폴더 읽기 중 오류 발생 ", e);
        }
    }

    // 이름으로 채널 조회
    @Override
    public Optional<Channel> findChannelUsingName(String channelName) {
        return findAllChannels().stream()
                .filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName))
                .findFirst();
    }

    // 아이디로 채널 조회
    @Override
    public Optional<Channel> findChannelUsingId(UUID channelId) {
        Path path = resolvePath(channelId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                Channel channel = (Channel) ois.readObject();
                return Optional.of(channel);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("FileChannelRepository: 아이디로 채널 조회 중 오류 발생 ", e);
            }
        }
        return Optional.empty();
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID channelId) {
        Path path = resolvePath(channelId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("FileChannelRepository: 채널 삭제 중 예외 발생 ", e);
        }
    }
}
