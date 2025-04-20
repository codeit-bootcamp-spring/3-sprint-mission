package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelService implements ChannelService {
    private Path directory;

    private void initDirectory() {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패:" + e);
            }
        }
    }

    public FileChannelService(Path directory) {
        this.directory = directory;
        initDirectory();
    }

    private Path resolvePath(UUID channelId) {
        return directory.resolve(channelId.toString().concat(".ser"));
    }

    // 저장 로직
    private void saveFile(Channel channel) {
        try (
                FileOutputStream fos = new FileOutputStream(resolvePath(channel.getId()).toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패: " + e);
        }
    }

    // 저장 로직
    private Optional<Channel> loadFile(UUID channelId) {
        Path path = resolvePath(channelId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                Channel loaded = (Channel) ois.readObject();
                if (!loaded.getId().equals(channelId)) {
                    throw new RuntimeException("파일명과 객체 ID 불일치: " + channelId + " vs " + loaded.getId());
                }
                return Optional.of(loaded);
            } catch (IOException | ClassNotFoundException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    // 비즈니스 로직
    @Override
    public Channel createChannel(Channel channel) {
        saveFile(channel);
        return channel;
    }

    // 비즈니스 로직
    @Override
    public Optional<Channel> getChannel(UUID channelId) {
        return loadFile(channelId);
    }

    // 비즈니스 로직
    @Override
    public List<Channel> getAllChannels() {
        if (Files.exists(directory)) {
            try {
                return Files.list(directory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                return (Channel) ois.readObject();
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("채널 파일 로딩 실패: " + path, e);
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
            } catch (IOException e) {
                throw new RuntimeException("채널 전체 조회 실패", e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    // 비즈니스 로직
    @Override
    public void updateChannel(Channel channel) {
        saveFile(channel);
    }

    // 비즈니스 로직
    @Override
    public void deleteChannel(UUID channelId) {
        try {
            Files.deleteIfExists(resolvePath(channelId));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패: " + channelId, e);
        }
    }
}