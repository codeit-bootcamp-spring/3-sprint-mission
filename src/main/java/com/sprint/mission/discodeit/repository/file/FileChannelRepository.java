package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Profile("file")
public class FileChannelRepository implements ChannelRepository {
    private final Path path;

    public FileChannelRepository(@Value("${storage.dirs.channels}") String dir) {
        this.path = Paths.get(dir);
        clearFile();
    }

    @Override
    public void save(Channel channel) {
        String filename = channel.getId().toString() + ".ser";
        Path file = path.resolve(filename);

        try (
                OutputStream out = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel loadByName(String name) {
        List<Channel> channels = loadAll();

        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel loadById(UUID id) {
        Path file = path.resolve(id.toString() + ".ser");
        return deserialize(file);
    }

    @Override
    public List<Channel> loadAll() {
        List<Channel> channels = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.ser")) {
            for (Path p : stream) {
                channels.add(deserialize(p));
            }
        } catch (IOException e) {
            throw new RuntimeException("[Channel] channels 폴더 접근 실패", e);
        }

        return channels;
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = loadById(id);
        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널입니다. (" + id + ")");
        }

        channel.updateName(name);
        save(channel);
        return channel;
    }

    @Override
    public void join(UUID userId, UUID channelId) {
        Channel channel = loadById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 channel 파일 (" + channelId + ".ser)");
        }

        channel.join(userId);
        save(channel);
        System.out.println(channel);
    }

    @Override
    public void leave(UUID userId, UUID channelId) {
        Channel channel = loadById(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 channel 파일 (" + channelId + ".ser)");
        }

        channel.leave(userId);
        save(channel);
        System.out.println(channel);
    }

    @Override
    public void delete(UUID id) {
        try {
            Path deletePath = path.resolve(id + ".ser");
            Files.deleteIfExists(deletePath);

        } catch (IOException e) {
            throw new RuntimeException("[Channel] 파일 삭제 실패 (" + id + ")", e);
        }
    }

    private Channel deserialize(Path file) {
        if (Files.notExists(file)) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 파일");
        }

        try (
                InputStream in = Files.newInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(in)
        ) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[Channel] Channel 파일 로드 실패", e);
        }
    }

    private void clearFile() {
        try {
            if (Files.exists(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path filePath : stream) {
                        Files.deleteIfExists(filePath);
                    }
                }
            } else {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
