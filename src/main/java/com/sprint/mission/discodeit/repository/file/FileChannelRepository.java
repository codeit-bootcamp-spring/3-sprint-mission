package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "File")
public class FileChannelRepository implements ChannelRepository {


    private static final Path FILE_PATH = Paths.get("src/main/java/com/sprint/mission/discodeit/repository/file/data/channels.ser");

    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<Channel> readFiles() {
        try {
            if (!Files.exists(FILE_PATH) || Files.size(FILE_PATH) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Channel> messages = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))) {
            while(true) {
                try {
                    messages.add((Channel) reader.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }


    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<Channel> channels) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))) {
                for (Channel channel : channels) {
                    writer.writeObject(channel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = readFiles();
        channels.add(channel);
        writeFiles(channels);
        return channel;
    }

    @Override
    public List<Channel> read() {
        List<Channel> channels = readFiles();
        return channels;
    }

    @Override
    public Optional<Channel> readById(UUID id) {
        List<Channel> channels = readFiles();
        Optional<Channel> channel = channels.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return channel;
    }

    @Override
    public void update(UUID id, Channel channel) {
        List<Channel> channels = readFiles();
        channels.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                    c.setChannelName(channel.getChannelName());
                    c.setDescription(channel.getDescription());
                });
        writeFiles(channels);
    }

    @Override
    public void delete(UUID channelId) {
        List<Channel> channels = readFiles();
        List<Channel> deleteChannels = channels.stream()
                .filter((c) -> !c.getId().equals(channelId))
                .toList();
        writeFiles(deleteChannels);
    }

}
