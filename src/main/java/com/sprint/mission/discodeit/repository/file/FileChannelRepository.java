package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final Path directory;

    public FileChannelRepository(Path directory) {
        this.directory = directory;
        initDirectory();
    }

    private void initDirectory() {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id.toString().concat(".ser"));
    }

    private Channel saveFile(Channel channel) {
        try (
                FileOutputStream fos = new FileOutputStream(resolvePath(channel.getId()).toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패: " + e);
        }
    }

    private Optional<Channel> loadFile(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                Channel loaded = (Channel) ois.readObject();
                return Optional.of(loaded);
            } catch (IOException | ClassNotFoundException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Channel save(Channel channel) {
        return saveFile(channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return loadFile(id);
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        if (Files.exists(directory)) {
            try {
                Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .forEach(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Channel channel = (Channel) ois.readObject();
                                if (channel != null) channels.add(channel);
                            } catch (IOException | ClassNotFoundException ignored) {}
                        });
            } catch (IOException e) {
                throw new RuntimeException("채널 전체 조회 실패", e);
            }
        }
        return channels;
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패: " + id, e);
        }
    }
}