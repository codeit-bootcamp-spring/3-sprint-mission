package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FileChannelRepository implements ChannelRepository {

    private final Path dir = Paths.get(System.getProperty("user.dir"), "data", "channels");

    public FileChannelRepository() {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("채널 디렉토리 생성 실패", e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path filePath = dir.resolve(channel.getId().toString().concat(".ser"));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패", e);
        }
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Path filePath = dir.resolve(id.toString().concat(".ser"));
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 로딩 실패", e);
        }
    }

    @Override
    public List<Channel> findAll() {
        if (!Files.exists(dir)) return new ArrayList<>();

        try {
            return Files.list(dir)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("채널 로딩 실패", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("채널 목록 불러오기 실패", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        Path filePath = dir.resolve(id.toString().concat(".ser"));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패", e);
        }
    }
}
