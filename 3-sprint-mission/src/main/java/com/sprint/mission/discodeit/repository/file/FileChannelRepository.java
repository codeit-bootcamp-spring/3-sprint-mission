package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "channel");

    public FileChannelRepository() {
        init();
    }

    // 저장할 경로의 파일 초기화
    public static Path init() {
        if (!Files.exists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return DIRECTORY;
    }

    public static Channel load(Path filePath){
        if (!Files.exists(filePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (Channel) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path filePath = Paths.get(String.valueOf(DIRECTORY), channel.getId()+".ser");
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        if (!Files.exists(DIRECTORY)) {
            return new ArrayList<>();
        } else {
            List<Channel> data = new ArrayList<>();
            File[] files = DIRECTORY.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    data.add(load(file.toPath()));
                }
            }
            return data;
        }
    }

    @Override
    public Channel find(UUID id) {
        return load(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public List<Channel> findByName(String name) {

        return findAll().stream()
                .filter(c -> c.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public void addEntry(UUID id, UUID userId) {
        find(id).addEntry(userId);
        save(find(id));
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.delete(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }
}
