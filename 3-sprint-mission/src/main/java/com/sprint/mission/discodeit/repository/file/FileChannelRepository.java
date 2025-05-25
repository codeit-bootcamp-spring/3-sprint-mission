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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if (!Files.exists(DIRECTORY)) {
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
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channelNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(channelNullable);
    }

    @Override
    public List<Channel> findByName(String name) {

        return findAll().stream()
                .filter(c -> c.getName().contains(name))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public boolean existsByName(String name) {
        return findAll().stream().anyMatch(c -> c.getName().contains(name));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.delete(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
