package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {

    private final Path channelDirectory;

    public FileChannelService() {
        this.channelDirectory = Paths.get(System.getProperty("user.dir"), "data", "channels");
        init();
    }

    private void init() {
        try {
            if (!Files.exists(channelDirectory)) {
                Files.createDirectories(channelDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 디렉토리 생성 실패", e);
        }
    }

    @Override
    public Channel create(Channel channel) {
        save(channel);
        return channel;
    }

    @Override
    public Channel getById(UUID id) {
        Path filePath = channelDirectory.resolve(id.toString().concat(".ser"));
        if (!Files.exists(filePath)) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Channel> getAll() {
        if (!Files.exists(channelDirectory)) return new ArrayList<>();

        try {
            return Files.list(channelDirectory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel update(Channel channel) {
        return create(channel);
    }

    @Override
    public void delete(UUID id) {
        Path filePath = channelDirectory.resolve(id.toString().concat(".ser"));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(Channel channel) {
        Path filePath = channelDirectory.resolve(channel.getId().toString().concat(".ser"));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
