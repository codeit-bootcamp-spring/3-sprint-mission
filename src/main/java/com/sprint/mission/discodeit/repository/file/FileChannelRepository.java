package com.sprint.mission.discodeit.repository.file;

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

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

@Repository
public class FileChannelRepository implements ChannelRepository {

    private final Path dataDirectory;

    public FileChannelRepository() {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), "data", "channels");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("채널 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getChannelPath(UUID channelId) {
        return dataDirectory.resolve(channelId.toString() + ".ser");
    }

    private void saveChannel(Channel channel) {
        Path channelPath = getChannelPath(channel.getChannelId());
        try (FileOutputStream fos = new FileOutputStream(channelPath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패: " + channel.getChannelId(), e);
        }
    }

    private Channel loadChannel(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 로드 실패: " + path, e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        saveChannel(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        Path channelPath = getChannelPath(channelId);
        if (Files.exists(channelPath)) {
            return loadChannel(channelPath);
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(dataDirectory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadChannel)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("채널 목록 로드 실패", e);
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        try {
            Files.deleteIfExists(getChannelPath(channelId));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패: " + channelId, e);
        }
    }
}
